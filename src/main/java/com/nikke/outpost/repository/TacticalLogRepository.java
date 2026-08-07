package com.nikke.outpost.repository;

import com.nikke.outpost.entity.TacticalLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacticalLogRepository extends JpaRepository<TacticalLog, Long> {

    // fetch all tactical logs associated with a specific unit ID
    List<TacticalLog> findByUnitId(Long unitId);
}