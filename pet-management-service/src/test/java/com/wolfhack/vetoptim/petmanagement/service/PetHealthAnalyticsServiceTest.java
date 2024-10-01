package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.PetHealthSummary;
import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import com.wolfhack.vetoptim.petmanagement.repository.MedicalRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetHealthAnalyticsServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private PetHealthAnalyticsService petHealthAnalyticsService;

    private List<MedicalRecord> medicalRecords;
    private MedicalRecord record1;
    private MedicalRecord record2;
    private MedicalRecord record3;

    @BeforeEach
    void setUp() {
        record1 = new MedicalRecord();
        record1.setDiagnosis("Cold");
        record1.setDateOfTreatment(LocalDate.of(2022, 1, 1));

        record2 = new MedicalRecord();
        record2.setDiagnosis("Cold");
        record2.setDateOfTreatment(LocalDate.of(2022, 3, 1));

        record3 = new MedicalRecord();
        record3.setDiagnosis("Fever");
        record3.setDateOfTreatment(LocalDate.of(2022, 2, 1));

        medicalRecords = List.of(record1, record3, record2);
    }

    @Test
    void testGetPetHealthSummary_WithRecords() {
        Long petId = 1L;

        when(medicalRecordRepository.findAllByPetId(petId)).thenReturn(medicalRecords);

        PetHealthSummary summary = petHealthAnalyticsService.getPetHealthSummary(petId);

        assertEquals(3, summary.getNumberOfVisits());
        assertEquals("Cold", summary.getLatestCondition());
        assertEquals("Cold", summary.getHealthTrend());

        verify(medicalRecordRepository).findAllByPetId(petId);
    }

    @Test
    void testGetPetHealthSummary_NoRecords() {
        Long petId = 1L;

        when(medicalRecordRepository.findAllByPetId(petId)).thenReturn(Collections.emptyList());

        PetHealthSummary summary = petHealthAnalyticsService.getPetHealthSummary(petId);

        assertEquals("No health records available.", summary.getLatestCondition());
        assertEquals(0, summary.getNumberOfVisits());

        verify(medicalRecordRepository).findAllByPetId(petId);
    }

    @Test
    void testGetPetHealthSummary_RecurringCondition() {
        Long petId = 1L;

        List<MedicalRecord> recurringRecords = List.of(record1, record2, record3);

        when(medicalRecordRepository.findAllByPetId(petId)).thenReturn(recurringRecords);

        PetHealthSummary summary = petHealthAnalyticsService.getPetHealthSummary(petId);

        assertEquals(3, summary.getNumberOfVisits());
        assertEquals("Cold", summary.getLatestCondition());
        assertEquals("Cold", summary.getHealthTrend());

        verify(medicalRecordRepository).findAllByPetId(petId);
    }

    @Test
    void testCalculateHealthTrend_NoRecurringConditions() {
        Long petId = 1L;

        when(medicalRecordRepository.findAllByPetId(petId)).thenReturn(List.of(record1, record3));

        PetHealthSummary summary = petHealthAnalyticsService.getPetHealthSummary(petId);

        assertEquals("No recurring conditions", summary.getHealthTrend());

        verify(medicalRecordRepository).findAllByPetId(petId);
    }
}