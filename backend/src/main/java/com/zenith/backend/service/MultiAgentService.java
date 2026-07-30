package com.zenith.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MultiAgentService {

    private final CompanyService companyService;
    private final ResumeService resumeService;
    private final TutoringService tutoringService;
    private final OpenRouterService openRouterService;

    @Autowired
    public MultiAgentService(CompanyService companyService,
                             ResumeService resumeService,
                             TutoringService tutoringService,
                             OpenRouterService openRouterService) {
        this.companyService = companyService;
        this.resumeService = resumeService;
        this.tutoringService = tutoringService;
        this.openRouterService = openRouterService;
    }

    public String runCollaborativeWorkflow(String targetCompany, String targetRole, String resumeText) {
        // Step 1: Resume Reviewer Agent runs analysis
        String resumeReport = resumeService.analyzeResume(resumeText);

        // Step 2: Company Research Agent retrieves company intel
        String companyReport = companyService.getCompanyInsights("Tell me about the interview rounds at " + targetCompany, new com.zenith.backend.model.Conversation());

        // Step 3: Tutoring Agent designs resource path
        String tutorPrompt = String.format("""
                Based on the following resume review and target company hiring profile, design a customized learning checklist for critical DSA and Core CS topics.
                
                Resume Review:
                %s
                
                Target Company Profile:
                %s
                
                Provide:
                - DSA topic checklist
                - System Design/CS Core subjects checklist
                - Recommended practice problems (LeetCode/custom)
                """, resumeReport, companyReport);
        String studyChecklist = openRouterService.ask("You are Zenith AI's specialized Tutoring Agent.", tutorPrompt);

        // Step 4: Career Mentor Agent synthesizes reports into a final plan
        String mentorPrompt = String.format("""
                You are the Career Mentor Agent. Synthesize the findings of our expert agent team into a unified, high-level Placement Readiness Report.
                
                Target Company: %s
                Target Role: %s
                
                1. Resume Reviewer Agent's Summary:
                %s
                
                2. Company Research Agent's Summary:
                %s
                
                3. Tutoring Agent's Study Checklist:
                %s
                
                Provide a unified Readiness Plan with:
                - Executive placement readiness rating (e.g. 75%% Ready)
                - High-priority actions (What to fix in the resume, what to study first)
                - Personalized 4-week timeline leading up to the interview
                
                Return clean, beautiful GitHub Markdown with clear headings.
                """, targetCompany, targetRole, resumeReport, companyReport, studyChecklist);

        return openRouterService.ask("You are Zenith AI's Career Mentor Agent.", mentorPrompt);
    }
}
