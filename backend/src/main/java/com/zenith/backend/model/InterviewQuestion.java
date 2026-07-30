package com.zenith.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interview_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interview_session_id", nullable = false)
    private Long interviewSessionId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String candidateAnswer;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String feedback;

    private Double technicalScore;

    private Double communicationScore;

    private Double confidenceScore;

    @Column(columnDefinition = "TEXT")
    private String fillerWordCounts; // JSON string of word counts

    private Integer questionOrder;
}
