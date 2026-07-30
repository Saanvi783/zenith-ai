package com.zenith.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EligibilityService {

    private final OpenRouterService openRouterService;

    @Autowired
    public EligibilityService(OpenRouterService openRouterService) {
        this.openRouterService = openRouterService;
    }

    public String checkEligibility(String query) {
        String systemPrompt = "You are Zenith AI's Eligibility Checker. You are an expert placement mentor.";
        String userPrompt = String.format("""
                Return clean GitHub Markdown.
                
                Rules:
                - Use ## headings.
                - Keep spacing compact.
                - Use bullet points.
                - No unnecessary blank lines.
                
                Analyze the following query:
                
                %s
                
                Return:
                
                ## Eligibility Status
                
                ## Reason
                
                ## Missing Requirements
                
                ## Preparation Advice
                """, query);

        return openRouterService.ask(systemPrompt, userPrompt);
    }
}
