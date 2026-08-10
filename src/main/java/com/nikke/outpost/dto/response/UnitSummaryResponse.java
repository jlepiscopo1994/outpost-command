package com.nikke.outpost.dto.response;

import com.nikke.outpost.enums.*;

import java.time.LocalDateTime;

public record UnitSummaryResponse(
        Long id,
        String name,
        String originIp,
        Manufacturer manufacturer,
        Element element,
        WeaponType weaponType,
        BurstType burstType,
        ClassType classType,
        LocalDateTime createdAt
) {}