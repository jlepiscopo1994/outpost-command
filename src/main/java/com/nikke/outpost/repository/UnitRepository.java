package com.nikke.outpost.repository;

import com.nikke.outpost.entity.Unit;
import com.nikke.outpost.enums.BurstType;
import com.nikke.outpost.enums.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    // Check for existing unit name (used for duplicate validation)
    boolean existsByName(String name);

    // Optional lookup by unique name
    Optional<Unit> findByName(String name);

    // Custom query methods for attribute filtering
    List<Unit> findByOriginIp(String originIp);

    List<Unit> findByManufacturer(Manufacturer manufacturer);

    List<Unit> findByBurstType(BurstType burstType);

    List<Unit> findByOriginIpAndBurstType(String originIp, BurstType burstType);
}