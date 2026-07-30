package com.zenith.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(nullable = false)
    private String role; // "user" or "assistant"

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String intent;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
