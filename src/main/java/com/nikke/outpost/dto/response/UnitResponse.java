package com.nikke.outpost.dto.response;

import com.nikke.outpost.entity.BurstSkill;
import com.nikke.outpost.entity.Skill;
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
        String imageUrl,
        SkillResponse skill1,
        SkillResponse skill2,
        BurstSkillResponse burstSkill,
        LocalDateTime createdAt,
        List<TacticalLogResponse> tacticalLogs
) {}