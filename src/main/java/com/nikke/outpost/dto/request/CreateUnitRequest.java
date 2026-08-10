package com.nikke.outpost.dto.request;

import com.nikke.outpost.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUnitRequest(
        @NotBlank(message = "Unit name is required")
        @Size(max = 100, message = "Unit name cannot exceed 100 characters")
        String name,

        @NotBlank(message = "Origin IP is required (e.g. 'Nikke', 'NieR: Automata', 'Stellar Blade', etc.)")
        String originIp,

        @NotNull(message = "Manufacturer is required")
        Manufacturer manufacturer,

        @NotNull(message = "Element is required")
        Element element,

        @NotNull(message = "Weapon type is required")
        WeaponType weaponType,

        @NotNull(message = "Burst type is required")
        BurstType burstType,

        @NotNull(message = "Class type is required")
        ClassType classType
) {}
