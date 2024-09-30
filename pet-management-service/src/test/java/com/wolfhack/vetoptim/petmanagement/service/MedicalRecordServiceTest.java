package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.event.task.EmergencyTaskCreationEvent;
import com.wolfhack.vetoptim.common.event.task.FollowUpTaskCreationEvent;
import com.wolfhack.vetoptim.petmanagement.event.EmergencyTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.event.FollowUpTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import com.wolfhack.vetoptim.petmanagement.model.Pet;
import com.wolfhack.vetoptim.petmanagement.repository.MedicalRecordRepository;
import com.wolfhack.vetoptim.petmanagement.repository.PetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private EmergencyTaskEventPublisher emergencyTaskEventPublisher;

    @Mock
    private FollowUpTaskEventPublisher followUpTaskEventPublisher;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private AutoCloseable openedMocks;

    @BeforeEach
    void setUp() {
        openedMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openedMocks.close();
    }

    @Test
    void testGetMedicalHistoryForPet() {
        Long petId = 1L;
        medicalRecordService.getMedicalHistoryForPet(petId);
        verify(medicalRecordRepository).findAllByPetId(petId);
    }

    @Test
    void testCreateMedicalRecord_Success() {
        Long petId = 1L;
        MedicalRecord medicalRecord = new MedicalRecord();
        when(petRepository.findById(petId)).thenReturn(Optional.of(new Pet()));

        medicalRecordService.createMedicalRecord(petId, medicalRecord);

        verify(medicalRecordRepository).save(medicalRecord);
        verify(emergencyTaskEventPublisher, never()).publishEmergencyTaskCreationEvent(any());
        verify(followUpTaskEventPublisher, never()).publishFollowUpTaskCreationEvent(any());
    }

    @Test
    void testUpdateMedicalRecord_Success() {
        Long recordId = 1L;
        MedicalRecord medicalRecordDetails = new MedicalRecord();
        MedicalRecord existingRecord = new MedicalRecord();
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(existingRecord));

        medicalRecordService.updateMedicalRecord(recordId, medicalRecordDetails);

        verify(medicalRecordRepository).save(existingRecord);
    }

    @Test
    void testDeleteMedicalRecord_Success() {
        Long recordId = 1L;
        medicalRecordService.deleteMedicalRecord(recordId);
        verify(medicalRecordRepository).deleteById(recordId);
    }

    @Test
    void testCreateMedicalRecordFromAppointment_Success() {
        Long petId = 1L;
        Pet pet = new Pet();
        pet.setId(petId);
        String diagnosis = "Checkup";
        String treatment = "Standard treatment";

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        MedicalRecord savedRecord = new MedicalRecord();
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(savedRecord);

        medicalRecordService.createMedicalRecordFromAppointment(petId, diagnosis, treatment);

        verify(medicalRecordRepository).save(any(MedicalRecord.class));
        verify(emergencyTaskEventPublisher, never()).publishEmergencyTaskCreationEvent(any(EmergencyTaskCreationEvent.class));
        verify(followUpTaskEventPublisher, never()).publishFollowUpTaskCreationEvent(any(FollowUpTaskCreationEvent.class));
    }

    @Test
    void testCreateMedicalRecordFromAppointment_WithCriticalCondition() {
        Long petId = 1L;
        Pet pet = new Pet();
        pet.setId(petId);
        String diagnosis = "Critical condition";
        String treatment = "Emergency treatment";

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        MedicalRecord savedRecord = new MedicalRecord();
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(savedRecord);

        medicalRecordService.createMedicalRecordFromAppointment(petId, diagnosis, treatment);

        verify(medicalRecordRepository).save(any(MedicalRecord.class));
        verify(emergencyTaskEventPublisher).publishEmergencyTaskCreationEvent(any(EmergencyTaskCreationEvent.class));
        verify(followUpTaskEventPublisher, never()).publishFollowUpTaskCreationEvent(any(FollowUpTaskCreationEvent.class));
    }
}