package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.petmanagement.model.Vaccination;
import com.wolfhack.vetoptim.petmanagement.service.VaccinationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VaccinationControllerTest {

    @Mock
    private VaccinationService vaccinationService;

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

        mockMvc.perform(get("/vaccinations/pet/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(vaccinationService).getVaccinationsForPet(1L);
    }

    @Test
    void testCreateVaccination() throws Exception {
        Vaccination vaccination = new Vaccination();
        when(vaccinationService.createVaccination(anyLong(), any(Vaccination.class))).thenReturn(vaccination);

        mockMvc.perform(post("/vaccinations/pet/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"vaccineName\": \"Rabies\"}"))
            .andExpect(status().isOk());

        verify(vaccinationService).createVaccination(anyLong(), any(Vaccination.class));
    }

    @Test
    void testUpdateVaccination() throws Exception {
        Vaccination vaccination = new Vaccination();
        when(vaccinationService.updateVaccination(anyLong(), any(Vaccination.class))).thenReturn(vaccination);

        mockMvc.perform(put("/vaccinations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"vaccineName\": \"Rabies\"}"))
            .andExpect(status().isOk());

        verify(vaccinationService).updateVaccination(anyLong(), any(Vaccination.class));
    }

    @Test
    void testDeleteVaccination() throws Exception {
        mockMvc.perform(delete("/vaccinations/1"))
            .andExpect(status().isNoContent());

        verify(vaccinationService).deleteVaccination(1L);
    }

}
