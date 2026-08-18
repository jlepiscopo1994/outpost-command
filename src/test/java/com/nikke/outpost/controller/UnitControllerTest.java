package com.nikke.outpost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nikke.outpost.dto.request.BurstSkillRequest;
import com.nikke.outpost.dto.request.CreateUnitRequest;
import com.nikke.outpost.dto.request.SkillRequest;
import com.nikke.outpost.dto.response.BurstSkillResponse;
import com.nikke.outpost.dto.response.SkillResponse;
import com.nikke.outpost.dto.response.UnitResponse;
import com.nikke.outpost.dto.response.UnitSummaryResponse;
import com.nikke.outpost.entity.BurstSkill;
import com.nikke.outpost.enums.*;
import com.nikke.outpost.exception.DuplicateResourceException;
import com.nikke.outpost.exception.GlobalExceptionHandler;
import com.nikke.outpost.exception.ResourceNotFoundException;
import com.nikke.outpost.service.UnitService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(UnitController.class)
@Import(GlobalExceptionHandler.class)
class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UnitService unitService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()); // Register JavaTimeModule for LocalDateTime serialization

    private CreateUnitRequest validCreateRequest;
    private UnitResponse sampleUnitResponse;
    private UnitSummaryResponse sampleSummaryResponse;

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules(); // Register modules for Java 8 date/time support

        SkillRequest sampleSkill1Request = new SkillRequest("FF Drill", "Deals continious piercing damage.");
        SkillRequest sampleSkill2Request = new SkillRequest("Tactical Reload", "Increase reload speed by 25.");
        BurstSkillRequest sampleBurstRequest = new BurstSkillRequest(
                "Absolute Penetration",
                "Deals massive damage to a single target.",
                BurstType.BURST_3,
                40
        );

        validCreateRequest = new CreateUnitRequest(
                "Rapi",
                "Nikke",
                Manufacturer.ELYSION,
                Element.FIRE,
                WeaponType.AR,
                BurstType.BURST_3,
                ClassType.ATTACKER,
                "https://raw.githubusercontent.com/fabulous/nikke-db/main/rapi.png",
                sampleSkill1Request,
                sampleSkill2Request,
                sampleBurstRequest
        );

        SkillResponse sampleSkill1Response = new SkillResponse("FF Drill", "Deals continious piercing damage.");
        SkillResponse sampleSkill2Response = new SkillResponse("Tactical Reload", "Increase reload speed by 25%.");
        BurstSkillResponse sampleBurstResponse = new BurstSkillResponse(
                "Absolute Penetration",
                "Deals massive damage to a single target.",
                BurstType.BURST_3,
                40
        );

        sampleUnitResponse = new UnitResponse(
                1L,
                "Rapi",
                "Nikke",
                Manufacturer.ELYSION,
                Element.FIRE,
                WeaponType.AR,
                BurstType.BURST_3,
                ClassType.ATTACKER,
                "https://raw.githubusercontent.com/fabulous/nikke-db/main/rapi.png",
                sampleSkill1Response,
                sampleSkill2Response,
                sampleBurstResponse,
                LocalDateTime.now(),
                List.of()
        );

        sampleSummaryResponse = new UnitSummaryResponse(
                1L,
                "Rapi",
                "Nikke",
                Manufacturer.ELYSION,
                Element.FIRE,
                WeaponType.AR,
                BurstType.BURST_3,
                ClassType.ATTACKER,
                "https://raw.githubusercontent.com/fabulous/nikke-db/main/rapi.png",
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("POST /api/v1/units - Create Unit")
    class CreateUnitEndpointTests {

        @Test
        @DisplayName("Should return 201 Created and response body when payload is valid")
        void createUnit_ValidPayload_Returns201Created() throws Exception {
            when(unitService.createUnit(any(CreateUnitRequest.class))).thenReturn(sampleUnitResponse);

            mockMvc.perform(post("/api/v1/units")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Rapi"))
                    .andExpect(jsonPath("$.originIp").value("Nikke"))
                    .andExpect(jsonPath("$.manufacturer").value("ELYSION"))
                    .andExpect(jsonPath("$.imageUrl").value("https://raw.githubusercontent.com/fabulous/nikke-db/main/rapi.png"))
                    .andExpect(jsonPath("$.skill1.name").value("FF Drill"))
                    .andExpect(jsonPath("$.skill2.name").value("Tactical Reload"))
                    .andExpect(jsonPath("$.burstSkill.name").value("Absolute Penetration"))
                    .andExpect(jsonPath("$.burstSkill.cooldown").value(40));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when validation fails (blank name)")
        void createUnit_BlankName_Returns400BadRequest() throws Exception {
            CreateUnitRequest invalidRequest = new CreateUnitRequest(
                    "", // Invalid blank name
                    "Nikke",
                    Manufacturer.ELYSION,
                    Element.FIRE,
                    WeaponType.AR,
                    BurstType.BURST_3,
                    ClassType.ATTACKER,
                    null,
                    null,
                    null,
                    null
            );

            mockMvc.perform(post("/api/v1/units")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 Conflict when DuplicateResourceException is thrown")
        void createUnit_DuplicateName_Returns409Conflict() throws Exception {
            when(unitService.createUnit(any(CreateUnitRequest.class)))
                    .thenThrow(new DuplicateResourceException("Unit with name 'Rapi' already exists"));

            mockMvc.perform(post("/api/v1/units")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Unit with name 'Rapi' already exists"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/units - List Units")
    class ListUnitsEndpointTests {

        @Test
        @DisplayName("Should return 200 OK and list of units")
        void listUnits_Returns200OK() throws Exception {
            when(unitService.getAllUnits(null, null)).thenReturn(List.of(sampleSummaryResponse));

            mockMvc.perform(get("/api/v1/units"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$[0].name").value("Rapi"))
                    .andExpect(jsonPath("$[0].imageUrl").value("https://raw.githubusercontent.com/fabulous/nikke-db/main/rapi.png"));
        }

        @Test
        @DisplayName("Should pass filter parameters to service layer")
        void getAllUnits_WithFilters_Returns200OK() throws Exception {
            when(unitService.getAllUnits("Nikke", BurstType.BURST_3))
                    .thenReturn(List.of(sampleSummaryResponse));

            mockMvc.perform(get("/api/v1/units")
                            .param("originIp", "Nikke")
                            .param("burstType", "BURST_3"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$[0].burstType").value("BURST_3"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/units/{id} - Get Unit by ID")
    class GetUnitByIdEndpointTests {

        @Test
        @DisplayName("Should return 200 OK and detailed unit response when ID exists")
        void getUnitById_Success_Returns200OK() throws Exception {
            when(unitService.getUnitById(1L)).thenReturn(sampleUnitResponse);

            mockMvc.perform(get("/api/v1/units/{id}", 1L))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Rapi"))
                    .andExpect(jsonPath("$.imageUrl").value("https://raw.githubusercontent.com/fabulous/nikke-db/main/rapi.png"))
                    .andExpect(jsonPath("$.skill1.name").value("FF Drill"))
                    .andExpect(jsonPath("$.burstSkill.cooldown").value(40));
        }

        @Test
        @DisplayName("Should return 404 Not Found when unit ID does not exist")
        void getUnitById_NotFound_Returns404NotFound() throws Exception {
            when(unitService.getUnitById(999L))
                    .thenThrow(new ResourceNotFoundException("Unit not found with id: 999"));

            mockMvc.perform(get("/api/v1/units/{id}", 999L))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Unit not found with id: 999"));
        }
    }
}