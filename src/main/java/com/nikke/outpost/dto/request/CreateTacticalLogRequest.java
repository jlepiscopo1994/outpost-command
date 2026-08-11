package com.nikke.outpost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for appending tactical notes to a unit")
public record CreateTacticalLogRequest(
        @Schema(description = "Short descriptive title of the tactical log", example = "Burst Skill Synergy & Team Composition Guide",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Log title is required")
        @Size(max = 150, message = "Title cannot exceed 150 characters")
        String title,

        @Schema(description = "Detailed combat mechanics, strategy analysis, or lore notes",
                example = "When paired with Burst II supporters, 2B gains an additional 15% ATK boost during full burst rotation.",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Log content is required")
        String content
) {}
