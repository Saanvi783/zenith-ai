package com.zenith.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssistantRequest {
    @NotBlank(message = "Query cannot be blank")
    private String query;
}
