package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.petmanagement.event.EmergencyTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.event.FollowUpTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.event.MedicalTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import com.wolfhack.vetoptim.petmanagement.model.Pet;
import com.wolfhack.vetoptim.petmanagement.repository.MedicalRecordRepository;
import com.wolfhack.vetoptim.petmanagement.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private MedicalTaskEventPublisher medicalTaskEventPublisher;

    @Mock
    private EmergencyTaskEventPublisher emergencyTaskEventPublisher;

    @Mock
    private FollowUpTaskEventPublisher followUpTaskEventPublisher;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private Pet pet;
    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setOwnerName("John Doe");

        medicalRecord = new MedicalRecord();
        medicalRecord.setId(1L);
        medicalRecord.setDiagnosis("Surgery");
        medicalRecord.setTreatment("Post-surgery care");
        medicalRecord.setDateOfTreatment(LocalDate.now());
        medicalRecord.setPet(pet);
    }

    @Test
    void testGetMedicalHistoryForPet() {
        when(medicalRecordRepository.findAllByPetId(pet.getId())).thenReturn(List.of(medicalRecord));

        List<MedicalRecord> result = medicalRecordService.getMedicalHistoryForPet(pet.getId());

        assertEquals(1, result.size());
        verify(medicalRecordRepository).findAllByPetId(pet.getId());
    }

    @Test
    void testCreateMedicalRecord_Success() {
        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);

        MedicalRecord result = medicalRecordService.createMedicalRecord(pet.getId(), medicalRecord);

        assertNotNull(result);
        verify(medicalRecordRepository).save(medicalRecord);
        verify(medicalTaskEventPublisher).publishMedicalTaskCreationEvent(any());
        verify(followUpTaskEventPublisher).publishFollowUpTaskCreationEvent(any());
    }

    @Test
    void testCreateMedicalRecord_Failure_PetNotFound() {
        when(petRepository.findById(pet.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordService.createMedicalRecord(pet.getId(), medicalRecord);
        });

        assertEquals("Pet not found", exception.getMessage());
        verify(medicalRecordRepository, never()).save(any());
        verify(medicalTaskEventPublisher, never()).publishMedicalTaskCreationEvent(any());
    }

    @Test
    void testCreateMedicalRecordFromAppointment_Success() {
        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);

        MedicalRecord result = medicalRecordService.createMedicalRecordFromAppointment(pet.getId(), "Diagnosis", "Treatment");

        assertNotNull(result);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
        verify(medicalTaskEventPublisher).publishMedicalTaskCreationEvent(any());
    }

    @Test
    void testCreateMedicalRecordFromAppointment_Failure_PetNotFound() {
        when(petRepository.findById(pet.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordService.createMedicalRecordFromAppointment(pet.getId(), "Diagnosis", "Treatment");
        });

        assertEquals("Pet not found", exception.getMessage());
        verify(medicalRecordRepository, never()).save(any());
        verify(medicalTaskEventPublisher, never()).publishMedicalTaskCreationEvent(any());
    }

    @Test
    void testUpdateMedicalRecord_Success() {
        Long recordId = 1L;
        MedicalRecord updatedRecord = new MedicalRecord();
        updatedRecord.setDiagnosis("Critical surgery");
        updatedRecord.setTreatment("Follow-up treatment");

        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(medicalRecord));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(updatedRecord);

        MedicalRecord result = medicalRecordService.updateMedicalRecord(recordId, updatedRecord);

        assertEquals("Critical surgery", result.getDiagnosis());
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
        verify(emergencyTaskEventPublisher).publishEmergencyTaskCreationEvent(any());
        verify(followUpTaskEventPublisher).publishFollowUpTaskCreationEvent(any());
        verify(medicalTaskEventPublisher).publishMedicalTaskCreationEvent(any());
    }

    @Test
    void testUpdateMedicalRecord_Failure_RecordNotFound() {
        Long recordId = 1L;
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordService.updateMedicalRecord(recordId, medicalRecord);
        });

        assertEquals("Medical record not found", exception.getMessage());
        verify(medicalRecordRepository, never()).save(any());
        verify(medicalTaskEventPublisher, never()).publishMedicalTaskCreationEvent(any());
    }

    @Test
    void testDeleteMedicalRecord() {
        Long recordId = 1L;
        doNothing().when(medicalRecordRepository).deleteById(recordId);

        medicalRecordService.deleteMedicalRecord(recordId);

        verify(medicalRecordRepository).deleteById(recordId);
    }

    @Test
    void testCriticalConditionTriggersEmergencyTask() {
        medicalRecord.setDiagnosis("Critical condition");

        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);

        medicalRecordService.createMedicalRecord(pet.getId(), medicalRecord);

        verify(emergencyTaskEventPublisher).publishEmergencyTaskCreationEvent(any());
    }

    @Test
    void testFollowUpRequiredTriggersFollowUpTask() {
        medicalRecord.setDiagnosis("Surgery required");

        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);

        medicalRecordService.createMedicalRecord(pet.getId(), medicalRecord);

        verify(followUpTaskEventPublisher).publishFollowUpTaskCreationEvent(any());
    }
}