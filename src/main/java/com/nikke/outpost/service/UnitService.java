package com.nikke.outpost.service;

import com.nikke.outpost.dto.request.CreateUnitRequest;
import com.nikke.outpost.dto.response.UnitResponse;
import com.nikke.outpost.dto.response.UnitSummaryResponse;
import com.nikke.outpost.enums.BurstType;

import java.util.List;

public interface UnitService {
    UnitResponse createUnit(CreateUnitRequest request);
    UnitResponse getUnitById(Long id);
    List<UnitSummaryResponse> getAllUnits(String originIp, BurstType burstType);
}
