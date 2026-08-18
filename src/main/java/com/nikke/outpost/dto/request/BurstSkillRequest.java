package com.nikke.outpost.dto.request;

import com.nikke.outpost.enums.BurstType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload for creating or updating a unit burst skill")
public record BurstSkillRequest(
        @Schema(description = "Name of the burst skill", example = "Absolute Penetration")
        @NotBlank(message = "Burst skill name is required")
        String name,

        @Schema(description = "Detailed burst effect, multipliers, and mechanics",
                example = "Deals 450% damage to target with piercing capabilities.")
        String description,

        @Schema(description = "Burst step classification", example = "BURST_3")
        @NotNull(message = "Burst skill type is required")
        BurstType burstType,

        @Schema(description = "Skill cooldown duration in seconds (usually 20s or 40s)", example = "40")
        @NotNull(message = "Cooldown in seconds is required")
        @Min(value = 1, message = "Cooldown must be at least 1 second")
        Integer cooldown
) {}
