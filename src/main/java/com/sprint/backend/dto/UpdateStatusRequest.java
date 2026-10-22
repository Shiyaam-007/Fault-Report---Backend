package com.sprint.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private String status; // OPEN | IN_PROGRESS | RESOLVED
}
