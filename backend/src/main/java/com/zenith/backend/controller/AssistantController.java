package com.zenith.backend.controller;

import com.zenith.backend.dto.AssistantRequest;
import com.zenith.backend.service.OrchestratorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin
public class AssistantController {

    private final OrchestratorService orchestratorService;

    @Autowired
    public AssistantController(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping("/assistant")
    public ResponseEntity<Map<String, Object>> assistant(@Valid @RequestBody AssistantRequest request) {
        try {
            Map<String, Object> result = orchestratorService.processQuery(request.getQuery());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> home() {
        return ResponseEntity.ok(Map.of("message", "Zenith AI Backend Running"));
    }
}
