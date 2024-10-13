package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.common.dto.pet.MedicalRecordDTO;
import com.wolfhack.vetoptim.petmanagement.service.IMedicalRecordService;
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
class MedicalRecordControllerTest {

    @Mock
    private IMedicalRecordService medicalRecordService;

    @InjectMocks
    private MedicalRecordController medicalRecordController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(medicalRecordController).build();
    }

    @Test
    void testGetMedicalHistory() throws Exception {
        when(medicalRecordService.getMedicalHistoryForPet(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/pets/1/medical-records"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(medicalRecordService).getMedicalHistoryForPet(1L);
    }

    @Test
    void testCreateMedicalRecord() throws Exception {
        MedicalRecordDTO medicalRecord = new MedicalRecordDTO();
        when(medicalRecordService.createMedicalRecord(anyLong(), any(MedicalRecordDTO.class))).thenReturn(medicalRecord);

        mockMvc.perform(post("/api/pets/1/medical-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"diagnosis\": \"test\"}"))
                .andExpect(status().isCreated());

        verify(medicalRecordService).createMedicalRecord(anyLong(), any(MedicalRecordDTO.class));
    }

    @Test
    void testUpdateMedicalRecord() throws Exception {
        MedicalRecordDTO updatedRecord = new MedicalRecordDTO();
        when(medicalRecordService.updateMedicalRecord(anyLong(), any(MedicalRecordDTO.class))).thenReturn(updatedRecord);

        mockMvc.perform(put("/api/pets/1/medical-records/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"diagnosis\": \"test\"}"))
                .andExpect(status().isOk());

        verify(medicalRecordService).updateMedicalRecord(anyLong(), any(MedicalRecordDTO.class));
    }

    @Test
    void testDeleteMedicalRecord() throws Exception {
        mockMvc.perform(delete("/api/pets/1/medical-records/1"))
                .andExpect(status().isNoContent());

        verify(medicalRecordService).deleteMedicalRecord(1L);
    }
}