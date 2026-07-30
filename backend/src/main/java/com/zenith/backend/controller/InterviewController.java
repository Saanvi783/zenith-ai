package com.zenith.backend.controller;

import com.zenith.backend.dto.AnswerRequest;
import com.zenith.backend.dto.InterviewRequest;
import com.zenith.backend.model.InterviewQuestion;
import com.zenith.backend.model.InterviewSession;
import com.zenith.backend.repository.InterviewQuestionRepository;
import com.zenith.backend.repository.InterviewSessionRepository;
import com.zenith.backend.service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
public class InterviewController {

    private final InterviewService interviewService;
    private final InterviewSessionRepository sessionRepository;
    private final InterviewQuestionRepository questionRepository;

    @Autowired
    public InterviewController(InterviewService interviewService,
                               InterviewSessionRepository sessionRepository,
                               InterviewQuestionRepository questionRepository) {
        this.interviewService = interviewService;
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
    }

    // --------------------------------------------------
    // Backward Compatibility Endpoints (Stateless)
    // --------------------------------------------------

    @PostMapping("/generate-interview")
    public ResponseEntity<Map<String, Object>> generateInterviewCompat(@RequestBody InterviewRequest request) {
        try {
            InterviewSession session = interviewService.startInterview(
                    null, // anonymous student
                    request.getCompany(),
                    request.getRole(),
                    request.getDifficulty(),
                    request.getTopic()
            );
            List<InterviewQuestion> firstQList = questionRepository.findByInterviewSessionIdOrderByQuestionOrderAsc(session.getId());
            String questionText = firstQList.isEmpty() ? "Please introduce yourself and explain your background." : firstQList.get(0).getQuestionText();
            
            return ResponseEntity.ok(Map.of("questions", List.of(questionText), "sessionId", session.getId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/analyze-answer")
    public ResponseEntity<Map<String, Object>> analyzeAnswerCompat(@RequestBody AnswerRequest request) {
        try {
            // Find the most recent active session
            Optional<InterviewSession> activeSession = sessionRepository.findFirstByActiveTrueOrderByCreatedAtDesc();
            if (activeSession.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No active mock interview session found. Start one first."));
            }
            
            Map<String, Object> result = interviewService.submitAnswer(activeSession.get().getId(), request.getAnswer());
            return ResponseEntity.ok(Map.of(
                    "feedback", result.get("questionFeedback"),
                    "filler_analysis", result.get("fillerAnalysis"),
                    "overall_score", result.get("questionScore")
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // --------------------------------------------------
    // Modern Stateful API Endpoints
    // --------------------------------------------------

    @PostMapping("/api/interview/start")
    public ResponseEntity<Map<String, Object>> startInterview(
            @RequestParam(required = false) Long studentId,
            @RequestBody InterviewRequest request) {
        try {
            InterviewSession session = interviewService.startInterview(
                    studentId,
                    request.getCompany(),
                    request.getRole(),
                    request.getDifficulty(),
                    request.getTopic()
            );
            List<InterviewQuestion> questions = questionRepository.findByInterviewSessionIdOrderByQuestionOrderAsc(session.getId());
            String questionText = questions.isEmpty() ? "Please introduce yourself." : questions.get(0).getQuestionText();
            return ResponseEntity.ok(Map.of(
                    "sessionId", session.getId(),
                    "question", questionText,
                    "order", 1
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/interview/submit-answer")
    public ResponseEntity<Map<String, Object>> submitAnswer(
            @RequestParam Long sessionId,
            @RequestBody Map<String, String> body) {
        try {
            String answer = body.get("answer");
            if (answer == null || answer.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Answer cannot be blank"));
            }
            Map<String, Object> result = interviewService.submitAnswer(sessionId, answer);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
