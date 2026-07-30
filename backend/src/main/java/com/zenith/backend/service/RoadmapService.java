package com.zenith.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoadmapService {

    private final OpenRouterService openRouterService;

    @Autowired
    public RoadmapService(OpenRouterService openRouterService) {
        this.openRouterService = openRouterService;
    }

    public String generateRoadmap(String query) {
        String systemPrompt = "You are Zenith AI's Placement Roadmap Generator. You are an expert placement mentor.";
        String userPrompt = String.format("""
                Return the response in clean GitHub Markdown.
                
                Rules:
                - Use ## headings.
                - Use bullet points.
                - Use tables only when useful.
                - No unnecessary blank lines.
                - Keep the response concise.
                - Make it look like ChatGPT.
                
                Create a personalized roadmap for:
                
                %s
                
                Include:
                - Timeline
                - Skills to Learn
                - Resources
                - Practice Plan
                - Projects
                - Tips
                """, query);

        return openRouterService.ask(systemPrompt, userPrompt);
    }
}
