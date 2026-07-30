package com.zenith.backend.controller;

import com.zenith.backend.dto.WorkflowRequest;
import com.zenith.backend.service.MultiAgentService;
import com.zenith.backend.service.TutoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class OrchestrationController {

    private final MultiAgentService multiAgentService;
    private final TutoringService tutoringService;

    @Autowired
    public OrchestrationController(MultiAgentService multiAgentService, TutoringService tutoringService) {
        this.multiAgentService = multiAgentService;
        this.tutoringService = tutoringService;
    }

    @PostMapping("/orchestration/workflow")
    public ResponseEntity<Map<String, Object>> runWorkflow(@RequestBody WorkflowRequest request) {
        try {
            String report = multiAgentService.runCollaborativeWorkflow(
                    request.getCompany(),
                    request.getRole(),
                    request.getResumeText()
            );
            return ResponseEntity.ok(Map.of("report", report));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/tutor/dsa")
    public ResponseEntity<Map<String, Object>> tutorDsa(@RequestBody Map<String, String> body) {
        try {
            String query = body.get("query");
            String response = tutoringService.tutorDsa(query);
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/tutor/evaluate-code")
    public ResponseEntity<Map<String, Object>> evaluateCode(@RequestBody Map<String, String> body) {
        try {
            String problemDescription = body.get("problemDescription");
            String candidateCode = body.get("candidateCode");
            String evaluation = tutoringService.evaluateCodeSubmission(problemDescription, candidateCode);
            return ResponseEntity.ok(Map.of("evaluation", evaluation));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/tutor/cs")
    public ResponseEntity<Map<String, Object>> tutorCs(@RequestBody Map<String, String> body) {
        try {
            String subject = body.get("subject");
            String query = body.get("query");
            String response = tutoringService.tutorCsSubject(subject, query);
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
