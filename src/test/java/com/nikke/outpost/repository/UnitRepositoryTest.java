package com.nikke.outpost.repository;

import com.nikke.outpost.entity.Unit;
import com.nikke.outpost.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UnitRepositoryTest {

    @Container
    @ServiceConnection // Automatically maps datasource URL, user, password and handles lifecycle
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("outpost_test_db")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UnitRepository unitRepository;

    private Unit rapi;
    private Unit anis;

    @BeforeEach
    void setUp() {
        unitRepository.deleteAll();

        rapi = Unit.builder()
                .name("Rapi")
                .originIp("Nikke")
                .manufacturer(Manufacturer.ELYSION)
                .element(Element.FIRE)
                .weaponType(WeaponType.AR)
                .burstType(BurstType.BURST_3)
                .classType(ClassType.ATTACKER)
                .build();

        anis = Unit.builder()
                .name("Anis")
                .originIp("Nikke")
                .manufacturer(Manufacturer.TETRA)
                .element(Element.IRON)
                .weaponType(WeaponType.RL)
                .burstType(BurstType.BURST_2)
                .classType(ClassType.DEFENDER)
                .build();
    }

    @Nested
    @DisplayName("Persist and Read Tests")
    class PersistAndReadTests {

        @Test
        @DisplayName("Should successfully persist unit and generate ID")
        void saveUnit_Success() {
            Unit savedUnit = unitRepository.save(rapi);

            assertThat(savedUnit.getId()).isNotNull();
            assertThat(savedUnit.getName()).isEqualTo("Rapi");
            assertThat(savedUnit.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should find unit by existing ID")
        void findById_Success() {
            Unit savedUnit = unitRepository.save(rapi);

            Optional<Unit> foundUnit = unitRepository.findById(savedUnit.getId());

            assertThat(foundUnit).isPresent();
            assertThat(foundUnit.get().getName()).isEqualTo("Rapi");
        }
    }

    @Nested
    @DisplayName("Query Method Tests")
    class QueryMethodTests {

        @Test
        @DisplayName("Should return true when checking existsByName for existing name")
        void existsByName_ReturnsTrue() {
            unitRepository.save(rapi);

            boolean exists = unitRepository.existsByName("Rapi");

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Should return false when checking existsByName for non-existent name")
        void existsByName_ReturnsFalse() {
            boolean exists = unitRepository.existsByName("UnknownUnit");

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("Should filter units by originIp and burstType")
        void findByOriginAndBurstType_Success() {
            unitRepository.save(rapi);
            unitRepository.save(anis);

            List<Unit> results = unitRepository.findByOriginIpAndBurstType("Nikke", BurstType.BURST_3);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getName()).isEqualTo("Rapi");
        }
    }

}