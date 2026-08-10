package com.nikke.outpost.service;

import com.nikke.outpost.dto.request.CreateUnitRequest;
import com.nikke.outpost.dto.response.TacticalLogResponse;
import com.nikke.outpost.dto.response.UnitResponse;
import com.nikke.outpost.dto.response.UnitSummaryResponse;
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

        // 3. Build and Persist Entity
        Unit unit = Unit.builder()
                .name(request.name().trim())
                .originIp(request.originIp().trim())
                .manufacturer(effectiveManufacturer)
                .element(request.element())
                .weaponType(request.weaponType())
                .burstType(request.burstType())
                .classType(request.classType())
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
                unit.getCreatedAt()
        );
    }
}
