package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import com.wolfhack.vetoptim.petmanagement.service.MedicalRecordService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordControllerTest {

    @Mock
    private MedicalRecordService medicalRecordService;

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

        mockMvc.perform(get("/pets/1/medical-records"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(medicalRecordService).getMedicalHistoryForPet(1L);
    }

    @Test
    void testCreateMedicalRecord() throws Exception {
        MedicalRecord medicalRecord = new MedicalRecord();
        when(medicalRecordService.createMedicalRecord(anyLong(), any(MedicalRecord.class))).thenReturn(medicalRecord);

        mockMvc.perform(post("/pets/1/medical-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"diagnosis\": \"test\"}"))
                .andExpect(status().isOk());

        verify(medicalRecordService).createMedicalRecord(anyLong(), any(MedicalRecord.class));
    }

    @Test
    void testUpdateMedicalRecord() throws Exception {
        MedicalRecord updatedRecord = new MedicalRecord();
        when(medicalRecordService.updateMedicalRecord(anyLong(), any(MedicalRecord.class))).thenReturn(updatedRecord);

        mockMvc.perform(put("/pets/1/medical-records/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"diagnosis\": \"test\"}"))
                .andExpect(status().isOk());

        verify(medicalRecordService).updateMedicalRecord(anyLong(), any(MedicalRecord.class));
    }

    @Test
    void testDeleteMedicalRecord() throws Exception {
        mockMvc.perform(delete("/pets/1/medical-records/1"))
                .andExpect(status().isNoContent());

        verify(medicalRecordService).deleteMedicalRecord(1L);
    }
}