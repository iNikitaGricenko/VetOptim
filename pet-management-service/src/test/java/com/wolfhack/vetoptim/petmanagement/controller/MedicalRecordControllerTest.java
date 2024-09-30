package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import com.wolfhack.vetoptim.petmanagement.service.MedicalRecordService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MedicalRecordControllerTest {

    @Mock
    private MedicalRecordService medicalRecordService;

    @InjectMocks
    private MedicalRecordController medicalRecordController;

	private AutoCloseable autoCloseable;

	@BeforeEach
    void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testGetMedicalHistory_Success() {
        Long petId = 1L;
        List<MedicalRecord> records = List.of(new MedicalRecord());
        when(medicalRecordService.getMedicalHistoryForPet(petId)).thenReturn(records);

        ResponseEntity<List<MedicalRecord>> response = medicalRecordController.getMedicalHistory(petId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(records, response.getBody());
    }

    @Test
    void testCreateMedicalRecord_Success() {
        Long petId = 1L;
        MedicalRecord medicalRecord = new MedicalRecord();
        when(medicalRecordService.createMedicalRecord(petId, medicalRecord)).thenReturn(medicalRecord);

        ResponseEntity<MedicalRecord> response = medicalRecordController.createMedicalRecord(petId, medicalRecord);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(medicalRecord, response.getBody());
    }

    @Test
    void testUpdateMedicalRecord_Success() {
        Long recordId = 1L;
        MedicalRecord medicalRecord = new MedicalRecord();
        when(medicalRecordService.updateMedicalRecord(recordId, medicalRecord)).thenReturn(medicalRecord);

        ResponseEntity<MedicalRecord> response = medicalRecordController.updateMedicalRecord(recordId, medicalRecord);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(medicalRecord, response.getBody());
    }

    @Test
    void testDeleteMedicalRecord_Success() {
        Long recordId = 1L;

        ResponseEntity<Void> response = medicalRecordController.deleteMedicalRecord(recordId);

        assertEquals(204, response.getStatusCode().value());
        verify(medicalRecordService).deleteMedicalRecord(recordId);
    }
}