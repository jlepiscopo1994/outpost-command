package com.nikke.outpost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for creating or updating a regular unit skill")
public record SkillRequest(

    @Schema(description = "Name of the skill", example = "Tactical Reload")
    @NotBlank(message = "Skill name is required")
    String name,

    @Schema(description = "Detailed skill effect and combat mechanics description",
            example = "Increase reload speed by 25% for 10 seconds.")
    String description
) {}