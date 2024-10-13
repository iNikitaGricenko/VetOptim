package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.common.dto.pet.VaccinationRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.VaccinationResponseDTO;
import com.wolfhack.vetoptim.petmanagement.service.IVaccinationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VaccinationControllerTest {

    @Mock
    private IVaccinationService vaccinationService;

    @InjectMocks
    private VaccinationController vaccinationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vaccinationController).build();
    }

    @Test
    void testGetVaccinationsForPet() throws Exception {
        when(vaccinationService.getVaccinationsForPet(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/vaccinations/pet/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(vaccinationService).getVaccinationsForPet(1L);
    }

    @Test
    void testCreateVaccination() throws Exception {
        VaccinationResponseDTO vaccinationResponseDTO = new VaccinationResponseDTO(1L, "Rabies", LocalDate.now(), LocalDate.now().plusMonths(6), 1L);
        when(vaccinationService.createVaccination(anyLong(), any(VaccinationRequestDTO.class))).thenReturn(vaccinationResponseDTO);

        mockMvc.perform(post("/api/vaccinations/pet/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"vaccineName\": \"Rabies\", \"vaccinationDate\": \"2023-10-01\", \"nextDueDate\": \"3024-04-01\"}"))
            .andExpect(status().isCreated());

        verify(vaccinationService).createVaccination(anyLong(), any(VaccinationRequestDTO.class));
    }

    @Test
    void testUpdateVaccination() throws Exception {
        VaccinationResponseDTO vaccinationResponseDTO = new VaccinationResponseDTO(1L, "Rabies", LocalDate.now(), LocalDate.now().plusMonths(6), 1L);
        when(vaccinationService.updateVaccination(anyLong(), any(VaccinationRequestDTO.class))).thenReturn(vaccinationResponseDTO);

        mockMvc.perform(put("/api/vaccinations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"vaccineName\": \"Rabies\", \"vaccinationDate\": \"2023-10-01\", \"nextDueDate\": \"3024-04-01\"}"))
            .andExpect(status().isOk());

        verify(vaccinationService).updateVaccination(anyLong(), any(VaccinationRequestDTO.class));
    }

    @Test
    void testDeleteVaccination() throws Exception {
        mockMvc.perform(delete("/api/vaccinations/1"))
            .andExpect(status().isNoContent());

        verify(vaccinationService).deleteVaccination(1L);
    }
}
