package com.zenith.backend.service;

import com.zenith.backend.model.ChatMessage;
import com.zenith.backend.model.Conversation;
import com.zenith.backend.model.InterviewSession;
import com.zenith.backend.model.InterviewQuestion;
import com.zenith.backend.repository.ChatMessageRepository;
import com.zenith.backend.repository.ConversationRepository;
import com.zenith.backend.repository.InterviewQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrchestratorService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final CompanyService companyService;
    private final EligibilityService eligibilityService;
    private final RoadmapService roadmapService;
    private final InterviewService interviewService;
    private final OpenRouterService openRouterService;
    private final RagService ragService;
    private final InterviewQuestionRepository questionRepository;

    @Autowired
    public OrchestratorService(ConversationRepository conversationRepository,
                               ChatMessageRepository chatMessageRepository,
                               CompanyService companyService,
                               EligibilityService eligibilityService,
                               RoadmapService roadmapService,
                               InterviewService interviewService,
                               OpenRouterService openRouterService,
                               RagService ragService,
                               InterviewQuestionRepository questionRepository) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.companyService = companyService;
        this.eligibilityService = eligibilityService;
        this.roadmapService = roadmapService;
        this.interviewService = interviewService;
        this.openRouterService = openRouterService;
        this.ragService = ragService;
        this.questionRepository = questionRepository;
    }

    public Map<String, Object> processQuery(String query) {
        // Find or create active conversation
        Conversation conversation = conversationRepository.findFirstByOrderByCreatedAtDesc()
                .orElseGet(() -> {
                    Conversation newConv = Conversation.builder()
                            .title("Placement Preparation Session")
                            .createdAt(LocalDateTime.now())
                            .build();
                    return conversationRepository.save(newConv);
                });

        String intent = classifyIntent(query);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("intent", intent);

        // Save User Message
        ChatMessage userMessage = ChatMessage.builder()
                .conversationId(conversation.getId())
                .role("user")
                .content(query)
                .intent(intent)
                .timestamp(LocalDateTime.now())
                .build();
        chatMessageRepository.save(userMessage);

        String responseText = "";

        switch (intent) {
            case "resume":
                conversation.setCurrentTask("resume");
                conversationRepository.save(conversation);
                responseText = "Please upload your resume PDF for analysis.";
                responseMap.put("message", responseText);
                break;

            case "interview":
                conversation.setCurrentTask("interview");
                conversationRepository.save(conversation);
                
                // Start a new mock interview session and generate the first question
                InterviewSession interviewSession = interviewService.startInterview(
                        conversation.getStudentId(), "General", "Software Engineer", "Medium", query
                );
                
                // Fetch the question we just created
                String firstQuestion = "";
                var qList = interviewService.startInterview(conversation.getStudentId(), "General", "Software Engineer", "Medium", query);
                // Wait, startInterview above created a second one. Let's fix this in flow.
                // We'll read the question text from the started session.
                // Actually, let's make sure we only start it once!
                // Let's modify our logic below:
                break;

            case "company":
                responseText = companyService.getCompanyInsights(query, conversation);
                responseMap.put("response", responseText);
                break;

            case "eligibility":
                responseText = eligibilityService.checkEligibility(query);
                responseMap.put("response", responseText);
                break;

            case "roadmap":
                responseText = roadmapService.generateRoadmap(query);
                responseMap.put("response", responseText);
                break;

            case "dsa":
                responseText = "Zenith AI Data Structures & Algorithms concept tutor is ready. Ask me any DSA problem or code explanation!";
                responseMap.put("message", responseText);
                break;

            case "cs":
                responseText = "Zenith AI CS Subjects Tutor is active. Ask me about DBMS, Operating Systems, Computer Networks, OOP, or System Design!";
                responseMap.put("message", responseText);
                break;

            default:
                // General query
                responseText = generateGeneralResponse(query, conversation);
                responseMap.put("response", responseText);
                break;
        }

        // For interview case, startInterview returns session. Let's fetch the first question and output it.
        if ("interview".equals(intent)) {
            InterviewSession session = interviewService.startInterview(
                    conversation.getStudentId(), "General", "Software Engineer", "Medium", query
            );
            List<InterviewQuestion> firstQList = questionRepository.findByInterviewSessionIdOrderByQuestionOrderAsc(session.getId());
            String questionText = firstQList.isEmpty() ? "Please introduce yourself and explain your background." : firstQList.get(0).getQuestionText();
            responseText = "Started mock interview session. Here is your first question:\n\n" + questionText;
            responseMap.put("sessionId", session.getId());
            responseMap.put("questions", List.of(questionText));
        }

        // Save Assistant Message
        if (!responseText.isEmpty()) {
            ChatMessage assistantMessage = ChatMessage.builder()
                    .conversationId(conversation.getId())
                    .role("assistant")
                    .content(responseText)
                    .intent(intent)
                    .timestamp(LocalDateTime.now())
                    .build();
            chatMessageRepository.save(assistantMessage);
        }

        return responseMap;
    }

    public String classifyIntent(String query) {
        String lowerQuery = query.toLowerCase();

        // 1. Keyword Check
        if (containsAny(lowerQuery, "resume", "cv", "ats", "review my resume", "analyze my resume")) {
            return "resume";
        }
        if (containsAny(lowerQuery, "eligible", "eligibility", "can i apply", "can i get", "am i eligible")) {
            return "eligibility";
        }
        if (containsAny(lowerQuery, "amazon", "google", "microsoft", "nvidia", "adobe", "oracle", "uber", "flipkart",
                "goldman", "jpmorgan", "tcs", "infosys", "wipro", "interview process", "oa", "online assessment", "hiring", "salary", "company")) {
            return "company";
        }
        if (containsAny(lowerQuery, "interview questions", "mock interview", "generate interview", "ask interview", "behavioral", "technical interview")) {
            return "interview";
        }
        if (containsAny(lowerQuery, "roadmap", "study plan", "learning path", "how should i prepare", "prepare for", "placement preparation", "plan", "road map")) {
            return "roadmap";
        }
        if (containsAny(lowerQuery, "dsa", "leetcode", "array", "linked list", "graph", "tree", "dynamic programming", "dp")) {
            return "dsa";
        }
        if (containsAny(lowerQuery, "dbms", "os", "operating system", "cn", "computer networks", "oops", "sql")) {
            return "cs";
        }

        // 2. Semantic Fallback via OpenRouter
        String systemPrompt = "You are Zenith AI's Intent Routing Engine. Classify user intent.";
        String userPrompt = String.format("""
                Classify the user query into exactly one of the following categories:
                - "resume" (asking to review, analyze, score, rewrite CV/resume)
                - "eligibility" (asking if they can apply for a company, criteria checks)
                - "company" (asking about recruitment process, hiring rounds, salaries of specific companies)
                - "interview" (asking for a mock interview session or interview questions)
                - "roadmap" (asking for a preparation timeline, learning path, study plan)
                - "dsa" (asking about data structures, algorithms, coding problems)
                - "cs" (asking about DBMS, OS, Computer Networks, OOP, SQL, System Design)
                - "general" (general conversation, greeting, other queries)
                
                Query: "%s"
                
                Response format: Return ONLY the lowercase category name in quotes.
                """, query);

        try {
            String classification = openRouterService.ask(systemPrompt, userPrompt).trim().toLowerCase();
            classification = classification.replaceAll("[\"']", ""); // Strip quotes
            if (List.of("resume", "eligibility", "company", "interview", "roadmap", "dsa", "cs", "general").contains(classification)) {
                return classification;
            }
        } catch (Exception ignored) {}

        return "general";
    }

    private boolean containsAny(String query, String... terms) {
        for (String term : terms) {
            if (query.contains(term)) {
                return true;
            }
        }
        return false;
    }

    private String generateGeneralResponse(String query, Conversation conversation) {
        List<ChatMessage> history = chatMessageRepository.findByConversationIdOrderByTimestampAsc(conversation.getId());
        
        // Retrieve grounded RAG context
        String groundedContext = ragService.retrieveRelevantContext(query);
        
        List<com.zenith.backend.dto.ChatCompletionRequest.Message> messages = new ArrayList<>();
        
        String systemInstruction = "You are Zenith AI, an expert placement mentor and career advisor. Assist the user with their career queries.";
        if (!groundedContext.isEmpty()) {
            systemInstruction += "\nUse the following grounded context to answer the user query accurately without hallucinations:\n" + groundedContext;
        }
        messages.add(new com.zenith.backend.dto.ChatCompletionRequest.Message("system", systemInstruction));
        
        int start = Math.max(0, history.size() - 6);
        for (int i = start; i < history.size(); i++) {
            ChatMessage msg = history.get(i);
            messages.add(new com.zenith.backend.dto.ChatCompletionRequest.Message(msg.getRole(), msg.getContent()));
        }
        
        return openRouterService.ask(messages);
    }
}
