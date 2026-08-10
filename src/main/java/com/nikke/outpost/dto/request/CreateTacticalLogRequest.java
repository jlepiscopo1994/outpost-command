package com.nikke.outpost.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTacticalLogRequest(
        @NotBlank(message = "Log title is required")
        @Size(max = 150, message = "Title cannot exceed 150 characters")
        String title,

        @NotBlank(message = "Log content is required")
        String content
) {}
