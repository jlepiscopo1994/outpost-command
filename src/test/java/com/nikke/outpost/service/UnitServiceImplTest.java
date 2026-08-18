package com.nikke.outpost.service;

import com.nikke.outpost.dto.request.BurstSkillRequest;
import com.nikke.outpost.dto.request.CreateUnitRequest;
import com.nikke.outpost.dto.request.SkillRequest;
import com.nikke.outpost.dto.response.UnitResponse;
import com.nikke.outpost.entity.BurstSkill;
import com.nikke.outpost.entity.Skill;
import com.nikke.outpost.entity.Unit;
import com.nikke.outpost.enums.*;
import com.nikke.outpost.exception.DuplicateResourceException;
import com.nikke.outpost.exception.ResourceNotFoundException;
import com.nikke.outpost.repository.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceImplTest {

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private UnitServiceImpl unitService;

    private CreateUnitRequest nativeRequest;
    private CreateUnitRequest crossoverRequest;
    private Unit savedNativeUnit;

    @BeforeEach
    void setUp() {
        SkillRequest skill1Req = new SkillRequest("FF Drill", "Deals continuous piercing damage.");
        SkillRequest skill2Req = new SkillRequest("Tactical Reload", "Increases reload speed by 25%.");
        BurstSkillRequest burstReq = new BurstSkillRequest(
                "Absolute Penetration",
                "Deals massive damage to a single target.",
                BurstType.BURST_3,
                40
        );

        nativeRequest = new CreateUnitRequest(
                "Rapi",
                "Nikke",
                Manufacturer.ELYSION,
                Element.FIRE,
                WeaponType.AR,
                BurstType.BURST_3,
                ClassType.ATTACKER,
                "https://raw.githubusercontent.com/fabulous/nikke-db/main/rapi.png",
                skill1Req,
                skill2Req,
                burstReq
        );

        crossoverRequest = new CreateUnitRequest(
                "2B",
                "NieR:Automata",
                Manufacturer.ELYSION, // Request specifies ELYSION, but business rules must enforce ABNORMAL!
                Element.FIRE,
                WeaponType.RL,
                BurstType.BURST_3,
                ClassType.DEFENDER,
                "https://raw.githubusercontent.com/fabulous/nikke-db/main/2b.png",
                null,
                null,
                null
        );

        Skill skill1 = Skill.builder()
                .name("FF Drill")
                .description("Deals continuous piercing damage.")
                .build();

        Skill skill2 = Skill.builder()
                .name("Tactical Reload")
                .description("Increases reload speed by 25%.")
                .build();

        BurstSkill burstSkill = BurstSkill.builder()
                .name("Absolute Penetration")
                .description("Deals massive damage to a single target.")
                .burstType(BurstType.BURST_3)
                .cooldown(40)
                .build();

        savedNativeUnit = Unit.builder()
                .id(1L)
                .name("Rapi")
                .originIp("Nikke")
                .manufacturer(Manufacturer.ELYSION)
                .element(Element.FIRE)
                .weaponType(WeaponType.AR)
                .burstType(BurstType.BURST_3)
                .classType(ClassType.ATTACKER)
                .imageUrl("https://raw.githubusercontent.com/fabulous/nikke-db/main/rapi.png")
                .skill1(skill1)
                .skill2(skill2)
                .burstSkill(burstSkill)
                .tacticalLogs(List.of())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Create Unit Tests")
    class CreateUnitTests {

        @Test
        @DisplayName("Should successfully create a native Nikke unit")
        void createUnit_NativeNikke_Success() {
            // Given
            when(unitRepository.existsByName(nativeRequest.name())).thenReturn(false);
            when(unitRepository.save(any(Unit.class))).thenReturn(savedNativeUnit);

            // When
            UnitResponse response = unitService.createUnit(nativeRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("Rapi");
            assertThat(response.originIp()).isEqualTo("Nikke");
            assertThat(response.manufacturer()).isEqualTo(Manufacturer.ELYSION);
            assertThat(response.imageUrl()).isEqualTo("https://raw.githubusercontent.com/fabulous/nikke-db/main/rapi.png");
            assertThat(response.skill1()).isNotNull();
            assertThat(response.skill1().name()).isEqualTo("FF Drill");
            assertThat(response.burstSkill()).isNotNull();
            assertThat(response.burstSkill().cooldown()).isEqualTo(40);

            verify(unitRepository, times(1)).existsByName("Rapi");
            verify(unitRepository, times(1)).save(any(Unit.class));
        }

        @Test
        @DisplayName("Should auto-assign ABNORMAL manufacturer for non-Nikke crossover origin IP")
        void createUnit_CrossoverIP_AutoAssignsAbnormalManufacturer() {
            // Given
            Unit savedCrossoverUnit = Unit.builder()
                    .id(2L)
                    .name("2B")
                    .originIp("NieR:Automata")
                    .manufacturer(Manufacturer.ABNORMAL)
                    .element(Element.FIRE)
                    .weaponType(WeaponType.RL)
                    .burstType(BurstType.BURST_3)
                    .classType(ClassType.DEFENDER)
                    .imageUrl("https://raw.githubusercontent.com/fabulous/nikke-db/main/2b.png")
                    .tacticalLogs(List.of())
                    .createdAt(LocalDateTime.now())
                    .build();

            when(unitRepository.existsByName(crossoverRequest.name())).thenReturn(false);
            when(unitRepository.save(any(Unit.class))).thenAnswer(invocation -> {
                Unit unitToSave = invocation.getArgument(0);
                // Assert that business rule mutated the manufacturer before persisting
                assertThat(unitToSave.getManufacturer()).isEqualTo(Manufacturer.ABNORMAL);
                return savedCrossoverUnit;
            });

            // When
            UnitResponse response = unitService.createUnit(crossoverRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.manufacturer()).isEqualTo(Manufacturer.ABNORMAL);
            verify(unitRepository, times(1)).save(any(Unit.class));
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when unit name already exists")
        void createUnit_DuplicateName_ThrowsException() {
            // Given
            when(unitRepository.existsByName(nativeRequest.name())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> unitService.createUnit(nativeRequest))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("Unit with name 'Rapi' already exists.");

            verify(unitRepository, times(1)).existsByName("Rapi");
            verify(unitRepository, never()).save(any(Unit.class));
        }
    }

    @Nested
    @DisplayName("Get Unit By ID Tests")
    class GetUnitByIDTests {

        @Test
        @DisplayName("Should return UnitResponse when unit exists")
        void getUnitById_Success() {
            // Given
            when(unitRepository.findById(1L)).thenReturn(Optional.of(savedNativeUnit));

            // When
            UnitResponse response = unitService.getUnitById(1L);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("Rapi");
            assertThat(response.skill1()).isNotNull();
            assertThat(response.skill1().name()).isEqualTo("FF Drill");
            verify(unitRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when unit ID does not exist")
        void getUnitByID_NotFound_ThrowsException() {
            // Given
            when(unitRepository.findById(999L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> unitService.getUnitById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Unit not found with id: 999");

            verify(unitRepository, times(1)).findById(999L);
        }
    }
}