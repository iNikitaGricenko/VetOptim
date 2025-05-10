package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.pet.MedicalRecordDTO;
import com.wolfhack.vetoptim.common.event.task.EmergencyTaskCreationEvent;
import com.wolfhack.vetoptim.common.event.task.FollowUpTaskCreationEvent;
import com.wolfhack.vetoptim.common.event.task.MedicalTaskCreationEvent;
import com.wolfhack.vetoptim.petmanagement.event.IEmergencyTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.event.IFollowUpTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.event.IMedicalTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.exception.MedicalRecordNotFoundException;
import com.wolfhack.vetoptim.petmanagement.exception.PetNotFoundException;
import com.wolfhack.vetoptim.petmanagement.mapper.MedicalRecordMapper;
import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import com.wolfhack.vetoptim.petmanagement.repository.MedicalRecordRepository;
import com.wolfhack.vetoptim.petmanagement.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MedicalRecordService implements IMedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PetRepository petRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    private final IMedicalTaskEventPublisher medicalTaskEventPublisher;
    private final IEmergencyTaskEventPublisher emergencyTaskEventPublisher;
    private final IFollowUpTaskEventPublisher followUpTaskEventPublisher;

    @Override
    public List<MedicalRecordDTO> getMedicalHistoryForPet(Long petId) {
        log.info("Fetching medical history for Pet ID: {}", petId);
        return medicalRecordRepository.findAllByPetId(petId)
            .stream()
            .map(medicalRecordMapper::toDTO)
            .toList();
    }

    @Override
    public MedicalRecordDTO createMedicalRecord(Long petId, MedicalRecordDTO medicalRecordDTO) {
        log.info("Creating medical record for Pet ID: {}", petId);
        return petRepository.findById(petId)
            .map(pet -> {
                MedicalRecord medicalRecord = medicalRecordMapper.toModel(medicalRecordDTO);
                medicalRecord.setPet(pet);
                MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);

                publishMedicalRecordEvents(petId, pet.getName(), medicalRecord);
                return medicalRecordMapper.toDTO(savedRecord);
            })
            .orElseThrow(() -> new PetNotFoundException(petId));
    }

    @Override
    public MedicalRecord createMedicalRecordFromAppointment(Long petId, String diagnosis, String treatment) {
        return petRepository.findById(petId)
            .map(pet -> {
                MedicalRecord medicalRecord = new MedicalRecord();
                medicalRecord.setDiagnosis(diagnosis);
                medicalRecord.setTreatment(treatment);
                medicalRecord.setPet(pet);
                medicalRecord.setDateOfTreatment(LocalDate.now());

                MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);

                publishMedicalRecordEvents(petId, pet.getName(), medicalRecord);

                return savedRecord;
            })
            .orElseThrow(() -> new PetNotFoundException(petId));
    }

    @Override
    public MedicalRecordDTO updateMedicalRecord(Long recordId, MedicalRecordDTO medicalRecordDTO) {
        log.info("Updating medical record with ID: {}", recordId);
        return medicalRecordRepository.findById(recordId)
            .map(existingRecord -> {
                medicalRecordMapper.updateMedicalRecordFromDTO(medicalRecordDTO, existingRecord);
                MedicalRecord updatedRecord = medicalRecordRepository.save(existingRecord);
                publishMedicalRecordEvents(updatedRecord.getPet().getId(), updatedRecord.getPet().getName(), updatedRecord);
                return medicalRecordMapper.toDTO(updatedRecord);
            })
            .orElseThrow(() -> new MedicalRecordNotFoundException(recordId));
    }

    @Override
    public void deleteMedicalRecord(Long recordId) {
        log.info("Deleting medical record with ID: {}", recordId);
        medicalRecordRepository.deleteById(recordId);
    }

    private void publishMedicalRecordEvents(Long petId, String petName, MedicalRecord medicalRecord) {
        if (isCriticalCondition(medicalRecord)) {
            emergencyTaskEventPublisher.publishEmergencyTaskCreationEvent(
                new EmergencyTaskCreationEvent(petId,
                    petName,
                    medicalRecord.getDiagnosis(),
                    "Emergency medical procedure required for " + petName)
            );
        }

        if (requiresFollowUp(medicalRecord)) {
            followUpTaskEventPublisher.publishFollowUpTaskCreationEvent(
                new FollowUpTaskCreationEvent(petId,
                    petName,
                    "Follow-up required for " + medicalRecord.getDiagnosis(),
                    LocalDate.now().plusWeeks(1).toString())
            );
        }

        medicalTaskEventPublisher.publishMedicalTaskCreationEvent(
            new MedicalTaskCreationEvent(petId,
                petName,
                medicalRecord.getPet().getOwnerName(),
                medicalRecord.getDiagnosis(),
                "Medical task created for treatment or follow-up")
        );
    }

    private boolean isCriticalCondition(MedicalRecord medicalRecord) {
        return medicalRecord.getDiagnosis().toLowerCase().contains("critical") ||
            medicalRecord.getDiagnosis().toLowerCase().contains("emergency");
    }

    private boolean requiresFollowUp(MedicalRecord medicalRecord) {
        return medicalRecord.getDiagnosis().toLowerCase().contains("surgery") ||
            medicalRecord.getDiagnosis().toLowerCase().contains("critical");
    }
}
