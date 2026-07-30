package com.zenith.backend.controller;

import com.zenith.backend.dto.ResumeAnalysisRequest;
import com.zenith.backend.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@CrossOrigin
public class ResumeController {

    private final ResumeService resumeService;

    @Autowired
    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/analyze-resume")
    public ResponseEntity<Map<String, Object>> analyzeResume(@RequestBody ResumeAnalysisRequest request) {
        try {
            String analysis = resumeService.analyzeResume(request.getResumeText());
            return ResponseEntity.ok(Map.of("analysis", analysis));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Uploaded file is empty."));
            }

            String resumeText;
            try (var inputStream = file.getInputStream()) {
                resumeText = resumeService.extractTextFromPdf(inputStream);
            }

            String analysis = resumeService.analyzeResume(resumeText);

            return ResponseEntity.ok(Map.of(
                    "intent", "resume",
                    "analysis", analysis
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
