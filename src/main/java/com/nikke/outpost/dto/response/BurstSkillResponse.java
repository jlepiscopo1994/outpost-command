package com.nikke.outpost.dto.response;

import com.nikke.outpost.enums.BurstType;

public record BurstSkillResponse(
        String name,
        String description,
        BurstType burstType,
        Integer cooldown
) {}
