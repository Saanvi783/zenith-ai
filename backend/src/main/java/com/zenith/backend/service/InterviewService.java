package com.zenith.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenith.backend.model.InterviewQuestion;
import com.zenith.backend.model.InterviewSession;
import com.zenith.backend.repository.InterviewQuestionRepository;
import com.zenith.backend.repository.InterviewSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class InterviewService {

    private final InterviewSessionRepository sessionRepository;
    private final InterviewQuestionRepository questionRepository;
    private final OpenRouterService openRouterService;
    private final ObjectMapper objectMapper;

    @Autowired
    public InterviewService(InterviewSessionRepository sessionRepository,
                            InterviewQuestionRepository questionRepository,
                            OpenRouterService openRouterService,
                            ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.openRouterService = openRouterService;
        this.objectMapper = objectMapper;
    }

    public InterviewSession startInterview(Long studentId, String company, String role, String difficulty, String topic) {
        // Deactivate any previous active sessions
        Optional<InterviewSession> activeSessionOpt = sessionRepository.findFirstByActiveTrueOrderByCreatedAtDesc();
        activeSessionOpt.ifPresent(s -> {
            s.setActive(false);
            sessionRepository.save(s);
        });

        InterviewSession session = InterviewSession.builder()
                .studentId(studentId)
                .company(company == null ? "General" : company)
                .role(role == null ? "Software Engineer" : role)
                .difficulty(difficulty == null ? "Medium" : difficulty)
                .topic(topic == null ? "General CS" : topic)
                .active(true)
                .build();

        session = sessionRepository.save(session);

        // Generate the first question
        String firstQuestionText = generateFirstQuestion(session);
        
        InterviewQuestion firstQuestion = InterviewQuestion.builder()
                .interviewSessionId(session.getId())
                .questionText(firstQuestionText)
                .questionOrder(1)
                .build();

        questionRepository.save(firstQuestion);

        return session;
    }

    public Map<String, Object> submitAnswer(Long sessionId, String answer) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        if (!session.isActive()) {
            throw new IllegalStateException("This interview session has already finished.");
        }

        List<InterviewQuestion> questions = questionRepository.findByInterviewSessionIdOrderByQuestionOrderAsc(sessionId);
        
        // Find the current question (the one that has no answer yet)
        InterviewQuestion currentQuestion = questions.stream()
                .filter(q -> q.getCandidateAnswer() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active question found to answer."));

        // Analyze filler words
        Map<String, Integer> fillerCounts = analyzeFillerWords(answer);
        int totalFillers = fillerCounts.values().stream().mapToInt(Integer::intValue).sum();

        // Call AI to evaluate answer
        Map<String, Object> evaluation = evaluateAnswer(currentQuestion.getQuestionText(), answer);
        
        currentQuestion.setCandidateAnswer(answer);
        currentQuestion.setFeedback((String) evaluation.get("feedback"));
        currentQuestion.setTechnicalScore(((Number) evaluation.get("technicalScore")).doubleValue());
        currentQuestion.setCommunicationScore(((Number) evaluation.get("communicationScore")).doubleValue());
        currentQuestion.setConfidenceScore(((Number) evaluation.get("confidenceScore")).doubleValue());
        
        try {
            currentQuestion.setFillerWordCounts(objectMapper.writeValueAsString(fillerCounts));
        } catch (Exception e) {
            currentQuestion.setFillerWordCounts("{}");
        }

        questionRepository.save(currentQuestion);

        int currentOrder = currentQuestion.getQuestionOrder();
        Map<String, Object> response = new HashMap<>();
        response.put("questionFeedback", currentQuestion.getFeedback());
        response.put("fillerAnalysis", Map.of("totalFillers", totalFillers, "counts", fillerCounts));
        
        // Calculate average score for the current question
        double qScore = (currentQuestion.getTechnicalScore() + currentQuestion.getCommunicationScore() + currentQuestion.getConfidenceScore()) / 3.0;
        response.put("questionScore", Math.round(qScore * 10.0) / 10.0);

        if (currentOrder < 5) {
            // Generate next adaptive question
            String nextQuestionText = generateNextQuestion(session, questions, currentQuestion.getFeedback());
            
            InterviewQuestion nextQuestion = InterviewQuestion.builder()
                    .interviewSessionId(session.getId())
                    .questionText(nextQuestionText)
                    .questionOrder(currentOrder + 1)
                    .build();

            questionRepository.save(nextQuestion);

            response.put("nextQuestion", nextQuestionText);
            response.put("isFinished", false);
        } else {
            // End interview session
            session.setActive(false);
            
            // Calculate final overall score
            List<InterviewQuestion> allQuestions = questionRepository.findByInterviewSessionIdOrderByQuestionOrderAsc(sessionId);
            double totalScoreSum = 0;
            for (InterviewQuestion q : allQuestions) {
                totalScoreSum += (q.getTechnicalScore() + q.getCommunicationScore() + q.getConfidenceScore()) / 3.0;
            }
            double overallScore = totalScoreSum / 5.0;
            
            // Apply filler words penalty (similar to python: max(1, 10 - total_fillers * 0.5))
            int grandTotalFillers = 0;
            for (InterviewQuestion q : allQuestions) {
                try {
                    JsonNode node = objectMapper.readTree(q.getFillerWordCounts());
                    grandTotalFillers += node.path("totalFillers").asInt(0);
                } catch (Exception ignored) {}
            }
            
            session.setOverallScore(Math.round(overallScore * 10.0) / 10.0);
            sessionRepository.save(session);

            response.put("isFinished", true);
            response.put("overallScore", session.getOverallScore());
            response.put("grandTotalFillers", grandTotalFillers);
            
            // Generate final summary feedback
            String summaryFeedback = generateSummaryFeedback(session, allQuestions);
            response.put("summaryFeedback", summaryFeedback);
        }

        return response;
    }

    private String generateFirstQuestion(InterviewSession session) {
        String systemPrompt = "You are Zenith AI's Interview Engine. You are a Senior Software Engineer.";
        String userPrompt = String.format("""
                You are a Senior Software Engineer at %s.
                Conduct a realistic technical mock interview for the role of %s.
                Difficulty: %s
                Focus Area: %s
                
                This is the first question of the interview.
                Generate a realistic, conversational technical interview question.
                
                Rules:
                - The question should sound like a real interviewer asking it in a live conversation.
                - Avoid dry LeetCode problem descriptions.
                - Focus on core concepts, design choices, or practical scenarios relevant to the focus area.
                - Return ONLY the question text. Do not add introductory or concluding remarks.
                """, session.getCompany(), session.getRole(), session.getDifficulty(), session.getTopic());

        return openRouterService.ask(systemPrompt, userPrompt).trim();
    }

    private String generateNextQuestion(InterviewSession session, List<InterviewQuestion> previousQuestions, String lastFeedback) {
        String history = previousQuestions.stream()
                .filter(q -> q.getCandidateAnswer() != null)
                .map(q -> String.format("Q: %s\nA: %s", q.getQuestionText(), q.getCandidateAnswer()))
                .collect(Collectors.joining("\n\n"));

        String systemPrompt = "You are Zenith AI's Interview Engine. You are a Senior Software Engineer.";
        String userPrompt = String.format("""
                You are a Senior Software Engineer at %s.
                Conduct a technical mock interview for the role of %s.
                Difficulty: %s
                Focus Area: %s
                
                So far, the interview has progressed through the following questions and answers:
                
                %s
                
                Feedback on the candidate's last response was:
                %s
                
                Based on the candidate's performance, generate the next question.
                It should either:
                1. Probe deeper into their previous answer to test depth of understanding.
                2. Transition to another important aspect of the focus area.
                
                Rules:
                - Sound conversational, like a real human interviewer.
                - Return ONLY the question text. Do not add introductory or concluding remarks.
                """, session.getCompany(), session.getRole(), session.getDifficulty(), session.getTopic(), history, lastFeedback);

        return openRouterService.ask(systemPrompt, userPrompt).trim();
    }

    private Map<String, Object> evaluateAnswer(String question, String answer) {
        String systemPrompt = "You are Zenith AI's Interview Evaluator. You are an experienced technical recruiter.";
        String userPrompt = String.format("""
                Evaluate the candidate's response to the following question.
                
                Interview Question:
                %s
                
                Candidate's Answer:
                %s
                
                Return a JSON object containing the evaluation.
                Format:
                {
                  "feedback": "A detailed Markdown summary of the evaluation including Strengths, Weaknesses, and Improvement Tips.",
                  "technicalScore": 7.5,
                  "communicationScore": 8.0,
                  "confidenceScore": 8.5
                }
                
                Ensure the response is ONLY valid JSON.
                """, question, answer);

        String jsonResponse = openRouterService.ask(systemPrompt, userPrompt);
        
        // Strip markdown code block wrappers if any
        jsonResponse = jsonResponse.replace("```json", "").replace("```", "").strip();

        Map<String, Object> result = new HashMap<>();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            result.put("feedback", root.path("feedback").asText("Good attempt. Keep practicing."));
            result.put("technicalScore", root.path("technicalScore").asDouble(7.0));
            result.put("communicationScore", root.path("communicationScore").asDouble(7.0));
            result.put("confidenceScore", root.path("confidenceScore").asDouble(7.0));
        } catch (Exception e) {
            // Fallback if parsing fails
            result.put("feedback", jsonResponse);
            result.put("technicalScore", 7.0);
            result.put("communicationScore", 7.0);
            result.put("confidenceScore", 7.0);
        }

        return result;
    }

    private String generateSummaryFeedback(InterviewSession session, List<InterviewQuestion> allQuestions) {
        String performanceHistory = allQuestions.stream()
                .map(q -> String.format("Q: %s\nScores - Tech: %.1f, Comm: %.1f, Conf: %.1f\nFeedback: %s",
                        q.getQuestionText(), q.getTechnicalScore(), q.getCommunicationScore(), q.getConfidenceScore(), q.getFeedback()))
                .collect(Collectors.joining("\n\n"));

        String systemPrompt = "You are Zenith AI's Placement Advisor. You are a career coach.";
        String userPrompt = String.format("""
                Generate a comprehensive final summary of the candidate's mock interview performance.
                
                Interview Details:
                - Company: %s
                - Role: %s
                - Difficulty: %s
                - Topic: %s
                - Overall Score: %.1f/10
                
                Detailed Round Log:
                %s
                
                Provide:
                - Executive Summary (overall strengths and performance level)
                - Technical Skills Gaps Identified
                - Communication & Soft Skills Feedback
                - Targeted 3-Week Preparation Action Plan (with resource links and topics)
                
                Return clean GitHub Markdown with ## headings.
                """, session.getCompany(), session.getRole(), session.getDifficulty(), session.getTopic(), session.getOverallScore(), performanceHistory);

        return openRouterService.ask(systemPrompt, userPrompt);
    }

    private Map<String, Integer> analyzeFillerWords(String answer) {
        Map<String, Integer> counts = new HashMap<>();
        String[] fillers = {"um", "uh", "like", "you know", "basically", "actually", "so"};
        for (String filler : fillers) {
            String regex = "\\b" + Pattern.quote(filler) + "\\b";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(answer);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            counts.put(filler, count);
        }
        return counts;
    }
}
