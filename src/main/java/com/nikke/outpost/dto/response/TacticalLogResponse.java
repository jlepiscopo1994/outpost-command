package com.nikke.outpost.dto.response;

import java.time.LocalDateTime;

public record TacticalLogResponse(
        Long id,
        Long unitId,
        String title,
        String content,
        LocalDateTime createdAt
) {}