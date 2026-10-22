package com.sprint.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Companion form-data fields sent alongside the multipart image upload.
 * The actual file part is received separately as MultipartFile in the controller.
 */
@Data
public class CreateTicketRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Reporter ID is required")
    private Long reporterId;
}
