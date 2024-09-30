package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.PetHealthSummary;
import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import com.wolfhack.vetoptim.petmanagement.repository.MedicalRecordRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetHealthAnalyticsServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private PetHealthAnalyticsService petHealthAnalyticsService;

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
    void testGetPetHealthSummary_WithRecords() {
        Long petId = 1L;
        List<MedicalRecord> medicalRecords = List.of(
            new MedicalRecord(null, "Flu", "Treatment", null, null),
            new MedicalRecord(null, "Surgery", "Follow-up", null, null)
        );
        when(medicalRecordRepository.findAllByPetId(petId)).thenReturn(medicalRecords);

        PetHealthSummary summary = petHealthAnalyticsService.getPetHealthSummary(petId);

        assertEquals(2, summary.getNumberOfVisits());
        assertEquals("Surgery", summary.getLatestCondition());
        assertEquals("Flu", summary.getHealthTrend());
    }

    @Test
    void testGetPetHealthSummary_WithoutRecords() {
        Long petId = 1L;
        when(medicalRecordRepository.findAllByPetId(petId)).thenReturn(List.of());

        PetHealthSummary summary = petHealthAnalyticsService.getPetHealthSummary(petId);

        assertEquals("No health records available.", summary.getLatestCondition());
    }
}