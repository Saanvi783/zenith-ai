package com.zenith.backend.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ResumeService {

    private final OpenRouterService openRouterService;

    @Autowired
    public ResumeService(OpenRouterService openRouterService) {
        this.openRouterService = openRouterService;
    }

    public String extractTextFromPdf(InputStream inputStream) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        try (PDDocument document = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    public String analyzeResume(String resumeText) {
        String systemPrompt = "You are Zenith AI's Resume Reviewer. You are an expert ATS Resume Reviewer and Technical Recruiter.";
        String userPrompt = String.format("""
                Return clean GitHub Markdown.
                
                Rules:
                - Use ## headings.
                - Keep spacing compact.
                - Use bullet points.
                - No unnecessary blank lines.
                
                Analyze this resume.
                
                Resume:
                
                %s
                
                Return:
                
                ## ATS Score
                
                ## Skills Found
                
                ## Missing Skills
                
                ## Strengths
                
                ## Weaknesses
                
                ## Projects Feedback
                
                ## Resume Improvements
                """, resumeText);

        return openRouterService.ask(systemPrompt, userPrompt);
    }
}
