package com.zenith.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnswerRequest {
    private String question;
    private String answer;
}
