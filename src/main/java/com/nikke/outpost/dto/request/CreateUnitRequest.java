package com.nikke.outpost.dto.request;

import com.nikke.outpost.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for registering a new combat unit")
public record CreateUnitRequest(
        @NotBlank(message = "Unit name is required")
        @Size(max = 100, message = "Unit name cannot exceed 100 characters")
        @Schema(description = "Name of the unit", example = "2B", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Originating IP/Franchise", example = "NieR:Automata",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Origin IP is required (e.g. 'Nikke', 'NieR: Automata', 'Stellar Blade', etc.)")
        String originIp,

        @Schema(description = "Manufacturer archetype (Auto-enforced to ABNORMAL if originIp is not Nikke)",
                example = "ELYSION", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Manufacturer is required")
        Manufacturer manufacturer,

        @Schema(description = "Elemental code", example = "FIRE", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Element is required")
        Element element,

        @Schema(description = "Weapon classification", example = "AR", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Weapon type is required")
        WeaponType weaponType,

        @Schema(description = "Burst skill tier", example = "BURST_3", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Burst type is required")
        BurstType burstType,

        @Schema(description = "Tactical battlefield role", example = "ATTACKER", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Class type is required")
        ClassType classType,

        @Schema(description = "Cloud asset URL or external image link",
                example = "httpa://raw.githubusercontent.com/fabulous/nikke-db/main/rapi.png", nullable = true)
        String imageUrl,

        @Schema(description = "Primary passive / active skill 1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Valid
        SkillRequest skill1,

        @Schema(description = "Primary passive / active skill 2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Valid
        SkillRequest skill2,

        @Schema(description = "Ultimate Burst skill", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Valid
        BurstSkillRequest burstSkill
) {}
