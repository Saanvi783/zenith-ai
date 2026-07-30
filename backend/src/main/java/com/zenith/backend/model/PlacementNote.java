package com.zenith.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "placement_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacementNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String category;

    private String tags;
}
