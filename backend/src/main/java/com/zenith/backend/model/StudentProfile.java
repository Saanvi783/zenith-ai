package com.zenith.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private Double cgpa;

    @Column(columnDefinition = "TEXT")
    private String skills;

    private String targetRoles;

    private String targetCompanies;
}
