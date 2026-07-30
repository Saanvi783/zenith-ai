package com.zenith.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TutoringService {

    private final OpenRouterService openRouterService;

    @Autowired
    public TutoringService(OpenRouterService openRouterService) {
        this.openRouterService = openRouterService;
    }

    public String tutorDsa(String query) {
        String systemPrompt = "You are Zenith AI's DSA Tutor Agent. You are an expert algorithms engineer and Java developer.";
        String userPrompt = String.format("""
                Explain the DSA concept or solve the problem requested by the candidate.
                
                Rules:
                - If the candidate asks for code, provide a high-quality, optimal Java solution.
                - Include time and space complexity analysis (Big O notation).
                - Suggest 2-3 similar LeetCode practice problems.
                - Format response in clean Markdown with clear headings.
                
                Query:
                %s
                """, query);

        return openRouterService.ask(systemPrompt, userPrompt);
    }

    public String evaluateCodeSubmission(String problemDescription, String candidateCode) {
        String systemPrompt = "You are Zenith AI's Code Evaluator. You analyze compilation correctness, correctness logic, and time/space complexity.";
        String userPrompt = String.format("""
                Evaluate the candidate's Java code submission.
                
                Problem Description:
                %s
                
                Candidate Code:
                %s
                
                Provide:
                1. Correctness analysis (Does it solve the problem? Are there edge cases missed?)
                2. Time and Space complexity optimization review.
                3. Refactored / Optimized code (if improvements can be made).
                
                Return clean GitHub Markdown with clear headings.
                """, problemDescription, candidateCode);

        return openRouterService.ask(systemPrompt, userPrompt);
    }

    public String tutorCsSubject(String subject, String query) {
        String systemPrompt = "You are Zenith AI's Core CS Tutor Agent. You are an expert in DBMS, Operating Systems, Computer Networks, and OOP.";
        String userPrompt = String.format("""
                Explain the Core CS concept requested.
                
                Subject:
                %s
                
                Query:
                %s
                
                Provide a clear explanation with diagrams/examples where possible, and list 2-3 common interview questions on this topic.
                Return clean GitHub Markdown.
                """, subject, query);

        return openRouterService.ask(systemPrompt, userPrompt);
    }
}
