package com.nikke.outpost.service;

import com.nikke.outpost.dto.request.CreateTacticalLogRequest;
import com.nikke.outpost.dto.response.TacticalLogResponse;

public interface TacticalLogService {
    TacticalLogResponse addLogToUnit(Long unitId, CreateTacticalLogRequest request);
}