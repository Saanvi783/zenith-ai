package com.zenith.backend.service;

import com.zenith.backend.model.Conversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CompanyService {

    private final OpenRouterService openRouterService;

    private static final List<String> COMPANIES = List.of(
        "Amazon", "Google", "Microsoft", "Adobe", "Oracle", "Goldman Sachs",
        "JPMorgan", "Flipkart", "Walmart", "Uber", "Atlassian", "NVIDIA",
        "Qualcomm", "Intel", "Apple", "Meta", "Netflix", "TCS", "Infosys",
        "Wipro", "Accenture", "Cognizant", "Capgemini", "Deloitte"
    );

    @Autowired
    public CompanyService(OpenRouterService openRouterService) {
        this.openRouterService = openRouterService;
    }

    public String extractCompany(String query) {
        String lowerQuery = query.toLowerCase();
        for (String company : COMPANIES) {
            if (lowerQuery.contains(company.toLowerCase())) {
                return company;
            }
        }
        return "General";
    }

    public String getCompanyInsights(String query, Conversation conversation) {
        String company = extractCompany(query);
        if (!"General".equals(company)) {
            conversation.setActiveCompany(company);
        } else if (conversation.getActiveCompany() != null) {
            company = conversation.getActiveCompany();
        }

        String systemPrompt = "You are Zenith AI's Company Intelligence Engine. You are an expert placement mentor.";
        String userPrompt = String.format("""
                Return the response in clean GitHub Markdown.
                
                Rules:
                - Use ## for section headings.
                - Use bullet points wherever possible.
                - Use tables only if they improve readability.
                - Do NOT add unnecessary blank lines.
                - Do NOT use excessive separators (---).
                - Keep spacing compact.
                - Be concise but complete.
                - Use only the sections relevant to the user's query.
                
                Company:
                %s
                
                User Query:
                %s
                
                Possible sections:
                - Hiring Process
                - Interview Rounds
                - Online Assessment (OA)
                - Important Topics
                - Skills Required
                - Expected Projects
                - Salary (Approx.)
                - Preparation Tips
                
                If the user asks about only one topic (e.g. salary or interview process), answer only that topic instead of all sections.
                """, company, query);

        return openRouterService.ask(systemPrompt, userPrompt);
    }
}
