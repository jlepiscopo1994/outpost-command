package com.nikke.outpost.service;

import com.nikke.outpost.dto.request.BurstSkillRequest;
import com.nikke.outpost.dto.request.CreateUnitRequest;
import com.nikke.outpost.dto.request.SkillRequest;
import com.nikke.outpost.dto.response.*;
import com.nikke.outpost.entity.BurstSkill;
import com.nikke.outpost.entity.Skill;
import com.nikke.outpost.entity.Unit;
import com.nikke.outpost.enums.BurstType;
import com.nikke.outpost.enums.Manufacturer;
import com.nikke.outpost.exception.DuplicateResourceException;
import com.nikke.outpost.exception.ResourceNotFoundException;
import com.nikke.outpost.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    @Override
    @Transactional
    public UnitResponse createUnit(CreateUnitRequest request) {
        // 1. Business Validation: check for duplicate unit name
        if (unitRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Unit with name '" + request.name() + "' already exists.");
        }

        // 2. Lore Rule Validation: External IPs must map to ABNORMAL manufacturer
        Manufacturer effectiveManufacturer = request.manufacturer();
        if (!"Nikke".equalsIgnoreCase(request.originIp().trim())) {
            effectiveManufacturer = Manufacturer.ABNORMAL;
        }

        // 3. Map Embeddable Skills
        Skill skill1 = mapToSkillEntity(request.skill1());
        Skill skill2 = mapToSkillEntity(request.skill2());
        BurstSkill burstSkill = mapToBurstSkillEntity(request.burstSkill());

        // 4. Build and Persist Entity
        Unit unit = Unit.builder()
                .name(request.name().trim())
                .originIp(request.originIp().trim())
                .manufacturer(effectiveManufacturer)
                .element(request.element())
                .weaponType(request.weaponType())
                .burstType(request.burstType())
                .classType(request.classType())
                .imageUrl(request.imageUrl())
                .skill1(skill1)
                .skill2(skill2)
                .burstSkill(burstSkill)
                .build();

        Unit savedUnit = unitRepository.save(unit);
        return mapToUnitResponse(savedUnit);
    }

    @Override
    public UnitResponse getUnitById(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + id));
        return mapToUnitResponse(unit);
    }

    @Override
    public List<UnitSummaryResponse> getAllUnits(String originIp, BurstType burstType) {
        List<Unit> units;

        if (originIp != null && burstType != null) {
            units = unitRepository.findByOriginIpAndBurstType(originIp, burstType);
        } else if (originIp != null) {
            units = unitRepository.findByOriginIp(originIp);
        } else if (burstType != null) {
            units = unitRepository.findByBurstType(burstType);
        } else {
            units = unitRepository.findAll();
        }

        return units.stream()
                .map(this::mapToUnitSummaryResponse)
                .toList();
    }

    // -- Helper Mapping Methods --

    private Skill mapToSkillEntity(SkillRequest request) {
        if (request == null) {
            return null;
        }
        return Skill.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }

    private BurstSkill mapToBurstSkillEntity(BurstSkillRequest request) {
        if (request == null) {
            return null;
        }
        return BurstSkill.builder()
                .name(request.name())
                .description(request.description())
                .burstType(request.burstType())
                .cooldown(request.cooldown())
                .build();
    }

    private SkillResponse mapToSkillResponse(Skill skill) {
        if (skill == null) {
            return null;
        }
        return new SkillResponse(
                skill.getName(),
                skill.getDescription()
        );
    }

    private BurstSkillResponse mapToBurstSkillResponse(BurstSkill burstSkill) {
        if (burstSkill == null) {
            return null;
        }
        return new BurstSkillResponse(
                burstSkill.getName(),
                burstSkill.getDescription(),
                burstSkill.getBurstType(),
                burstSkill.getCooldown()
        );
    }

    private UnitResponse mapToUnitResponse(Unit unit) {
        List<TacticalLogResponse> logs = unit.getTacticalLogs() == null ? List.of() :
                unit.getTacticalLogs().stream()
                        .map(log -> new TacticalLogResponse(
                                log.getId(),
                                unit.getId(),
                                log.getTitle(),
                                log.getContent(),
                                log.getCreatedAt()
                        )).toList();
        return new UnitResponse(
                unit.getId(),
                unit.getName(),
                unit.getOriginIp(),
                unit.getManufacturer(),
                unit.getElement(),
                unit.getWeaponType(),
                unit.getBurstType(),
                unit.getClassType(),
                unit.getImageUrl(),
                mapToSkillResponse(unit.getSkill1()),
                mapToSkillResponse(unit.getSkill2()),
                mapToBurstSkillResponse(unit.getBurstSkill()),
                unit.getCreatedAt(),
                logs
        );
    }

    private UnitSummaryResponse mapToUnitSummaryResponse(Unit unit) {
        return new UnitSummaryResponse(
                unit.getId(),
                unit.getName(),
                unit.getOriginIp(),
                unit.getManufacturer(),
                unit.getElement(),
                unit.getWeaponType(),
                unit.getBurstType(),
                unit.getClassType(),
                unit.getImageUrl(),
                unit.getCreatedAt()
        );
    }
}
