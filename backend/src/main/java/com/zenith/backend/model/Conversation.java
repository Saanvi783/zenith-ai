package com.zenith.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Long studentId;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String currentTask;

    private String activeCompany;
}
