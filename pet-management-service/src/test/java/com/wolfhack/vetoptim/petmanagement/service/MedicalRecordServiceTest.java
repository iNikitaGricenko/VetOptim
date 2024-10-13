package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.pet.MedicalRecordDTO;
import com.wolfhack.vetoptim.petmanagement.event.IEmergencyTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.event.IFollowUpTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.event.IMedicalTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.exception.MedicalRecordNotFoundException;
import com.wolfhack.vetoptim.petmanagement.exception.PetNotFoundException;
import com.wolfhack.vetoptim.petmanagement.mapper.MedicalRecordMapper;
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
    private IMedicalTaskEventPublisher medicalTaskEventPublisher;

    @Mock
    private IEmergencyTaskEventPublisher emergencyTaskEventPublisher;

    @Mock
    private IFollowUpTaskEventPublisher followUpTaskEventPublisher;

    @Mock
    private MedicalRecordMapper medicalRecordMapper;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private Pet pet;
    private MedicalRecord medicalRecord;
    private MedicalRecordDTO medicalRecordDTO;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setOwnerName("John Doe");

        medicalRecord = new MedicalRecord();
        medicalRecord.setId(1L);
        medicalRecord.setDiagnosis("Critical condition");
        medicalRecord.setTreatment("Emergency treatment");
        medicalRecord.setDateOfTreatment(LocalDate.now());
        medicalRecord.setPet(pet);

        medicalRecordDTO = new MedicalRecordDTO(1L, "Critical condition", "Emergency treatment", LocalDate.now(), pet.getId());
    }

    @Test
    void testGetMedicalHistoryForPet() {
        when(medicalRecordRepository.findAllByPetId(pet.getId())).thenReturn(List.of(medicalRecord));
        when(medicalRecordMapper.toDTO(any(MedicalRecord.class))).thenReturn(medicalRecordDTO);

        List<MedicalRecordDTO> result = medicalRecordService.getMedicalHistoryForPet(pet.getId());

        assertEquals(1, result.size());
        verify(medicalRecordRepository).findAllByPetId(pet.getId());
        verify(medicalRecordMapper).toDTO(any(MedicalRecord.class));
    }

    @Test
    void testCreateMedicalRecord_Success() {
        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);
        when(medicalRecordMapper.toModel(any(MedicalRecordDTO.class))).thenReturn(medicalRecord);
        when(medicalRecordMapper.toDTO(any(MedicalRecord.class))).thenReturn(medicalRecordDTO);

        MedicalRecordDTO result = medicalRecordService.createMedicalRecord(pet.getId(), medicalRecordDTO);

        assertNotNull(result);
        verify(medicalRecordRepository).save(medicalRecord);
        verify(medicalTaskEventPublisher).publishMedicalTaskCreationEvent(any());
        verify(followUpTaskEventPublisher).publishFollowUpTaskCreationEvent(any());
    }

    @Test
    void testCreateMedicalRecord_Failure_PetNotFound() {
        when(petRepository.findById(pet.getId())).thenReturn(Optional.empty());

        PetNotFoundException exception = assertThrows(PetNotFoundException.class, () -> {
            medicalRecordService.createMedicalRecord(pet.getId(), medicalRecordDTO);
        });

        assertEquals("Pet not found with ID: " + pet.getId(), exception.getMessage());
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

        PetNotFoundException exception = assertThrows(PetNotFoundException.class, () -> {
            medicalRecordService.createMedicalRecordFromAppointment(pet.getId(), "Diagnosis", "Treatment");
        });

        assertEquals("Pet not found with ID: " + pet.getId(), exception.getMessage());
        verify(medicalRecordRepository, never()).save(any());
        verify(medicalTaskEventPublisher, never()).publishMedicalTaskCreationEvent(any());
    }

    @Test
    void testUpdateMedicalRecord_Success() {
        Long recordId = 1L;
        MedicalRecordDTO updatedRecordDTO = new MedicalRecordDTO();
        updatedRecordDTO.setDiagnosis("Critical surgery");
        updatedRecordDTO.setTreatment("Follow-up treatment");

        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(medicalRecord));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);
        when(medicalRecordMapper.toDTO(any(MedicalRecord.class))).thenReturn(updatedRecordDTO);

        MedicalRecordDTO result = medicalRecordService.updateMedicalRecord(recordId, updatedRecordDTO);

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

        MedicalRecordNotFoundException exception = assertThrows(MedicalRecordNotFoundException.class, () -> {
            medicalRecordService.updateMedicalRecord(recordId, medicalRecordDTO);
        });

        assertEquals("Medical record not found with ID: " + recordId, exception.getMessage());
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
        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));
        when(medicalRecordMapper.toModel(any(MedicalRecordDTO.class))).thenReturn(medicalRecord);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);
        when(medicalRecordMapper.toDTO(any(MedicalRecord.class))).thenReturn(medicalRecordDTO);

        medicalRecordService.createMedicalRecord(pet.getId(), medicalRecordDTO);

        verify(emergencyTaskEventPublisher).publishEmergencyTaskCreationEvent(any());
    }

    @Test
    void testFollowUpRequiredTriggersFollowUpTask() {
        medicalRecordDTO.setDiagnosis("Surgery required");

        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));
        when(medicalRecordMapper.toModel(any(MedicalRecordDTO.class))).thenReturn(medicalRecord);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);
        when(medicalRecordMapper.toDTO(any(MedicalRecord.class))).thenReturn(medicalRecordDTO);

        medicalRecordService.createMedicalRecord(pet.getId(), medicalRecordDTO);

        verify(followUpTaskEventPublisher).publishFollowUpTaskCreationEvent(any());
    }
}
