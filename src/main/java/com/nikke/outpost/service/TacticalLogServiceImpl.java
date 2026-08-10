package com.nikke.outpost.service;

import com.nikke.outpost.dto.request.CreateTacticalLogRequest;
import com.nikke.outpost.dto.response.TacticalLogResponse;
import com.nikke.outpost.entity.TacticalLog;
import com.nikke.outpost.entity.Unit;
import com.nikke.outpost.exception.ResourceNotFoundException;
import com.nikke.outpost.repository.TacticalLogRepository;
import com.nikke.outpost.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TacticalLogServiceImpl implements  TacticalLogService {

    private final UnitRepository unitRepository;
    private final TacticalLogRepository tacticalLogRepository;

    @Override
    @Transactional
    public TacticalLogResponse addLogToUnit(Long unitId, CreateTacticalLogRequest request) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot add log. Unit not found with id: " + unitId));

        TacticalLog log = TacticalLog.builder()
                .title(request.title().trim())
                .content(request.content().trim())
                .unit(unit)
                .build();

        TacticalLog savedLog = tacticalLogRepository.save(log);

        return new TacticalLogResponse(
                savedLog.getId(),
                unit.getId(),
                savedLog.getTitle(),
                savedLog.getContent(),
                savedLog.getCreatedAt()
        );
    }
}
