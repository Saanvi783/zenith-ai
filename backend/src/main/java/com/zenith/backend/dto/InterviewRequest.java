package com.zenith.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InterviewRequest {
    private String company;
    private String role;
    private String difficulty;
    private String topic;
}
