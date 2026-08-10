package com.nikke.outpost.dto.response;

import com.nikke.outpost.enums.*;

import java.time.LocalDateTime;
import java.util.List;

public record UnitResponse(
        Long id,
        String name,
        String originIp,
        Manufacturer manufacturer,
        Element element,
        WeaponType weaponType,
        BurstType burstType,
        ClassType classType,
        LocalDateTime createdAt,
        List<TacticalLogResponse> tacticalLogs
) {}