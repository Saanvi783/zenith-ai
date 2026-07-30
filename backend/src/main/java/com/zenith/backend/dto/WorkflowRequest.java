package com.zenith.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorkflowRequest {
    private String company;
    private String role;
    private String resumeText;
}
