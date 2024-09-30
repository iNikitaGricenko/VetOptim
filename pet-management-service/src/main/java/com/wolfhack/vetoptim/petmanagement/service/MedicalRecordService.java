package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.event.task.EmergencyTaskCreationEvent;
import com.wolfhack.vetoptim.common.event.task.FollowUpTaskCreationEvent;
import com.wolfhack.vetoptim.common.event.task.MedicalTaskCreationEvent;
import com.wolfhack.vetoptim.petmanagement.event.EmergencyTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.event.FollowUpTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.event.MedicalTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import com.wolfhack.vetoptim.petmanagement.repository.MedicalRecordRepository;
import com.wolfhack.vetoptim.petmanagement.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PetRepository petRepository;
    private final MedicalTaskEventPublisher medicalTaskEventPublisher;
    private final EmergencyTaskEventPublisher emergencyTaskEventPublisher;
    private final FollowUpTaskEventPublisher followUpTaskEventPublisher;

    public List<MedicalRecord> getMedicalHistoryForPet(Long petId) {
        log.info("Fetching medical history for Pet ID: {}", petId);
        return medicalRecordRepository.findAllByPetId(petId);
    }

    public MedicalRecord createMedicalRecord(Long petId, MedicalRecord medicalRecord) {
        log.info("Creating medical record for Pet ID: {}", petId);
        return petRepository.findById(petId)
            .map(pet -> {
                medicalRecord.setPet(pet);
                MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
                log.info("Medical record created with ID: {} for Pet ID: {}", savedRecord.getId(), petId);

                if (isCriticalCondition(medicalRecord)) {
                    log.info("Critical condition detected for Pet ID: {}. Publishing emergency task event.", petId);
                    emergencyTaskEventPublisher.publishEmergencyTaskCreationEvent(
                        new EmergencyTaskCreationEvent(petId, pet.getName(),
                            medicalRecord.getDiagnosis(), "Emergency medical procedure required for " + pet.getName()));
                }

                if (requiresFollowUp(medicalRecord)) {
                    log.info("Follow-up required for Pet ID: {}. Publishing follow-up task event.", petId);
                    followUpTaskEventPublisher.publishFollowUpTaskCreationEvent(
                        new FollowUpTaskCreationEvent(petId, pet.getName(),
                            "Follow-up required for " + medicalRecord.getDiagnosis(),
                            LocalDate.now().plusWeeks(1).toString()));
                }

                log.info("Publishing medical task creation event for Pet ID: {}", petId);
                MedicalTaskCreationEvent event = new MedicalTaskCreationEvent(
                    petId,
                    pet.getName(),
                    pet.getOwnerName(),
                    medicalRecord.getDiagnosis(),
                    "Medical task created for treatment or follow-up"
                );
                medicalTaskEventPublisher.publishMedicalTaskCreationEvent(event);

                return savedRecord;
            })
            .orElseThrow(() -> {
                log.error("Pet not found with ID: {}", petId);
                return new RuntimeException("Pet not found");
            });
    }

    public MedicalRecord createMedicalRecordFromAppointment(Long petId, String diagnosis, String treatment) {
        return petRepository.findById(petId)
            .map(pet -> {
                MedicalRecord medicalRecord = new MedicalRecord();
                medicalRecord.setDiagnosis(diagnosis);
                medicalRecord.setTreatment(treatment);
                medicalRecord.setPet(pet);
                medicalRecord.setDateOfTreatment(LocalDate.now());

                MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);

                if (isCriticalCondition(medicalRecord)) {
                    emergencyTaskEventPublisher.publishEmergencyTaskCreationEvent(
                        new EmergencyTaskCreationEvent(petId, pet.getName(),
                            medicalRecord.getDiagnosis(), "Emergency medical procedure required for " + pet.getName()));
                }

                if (requiresFollowUp(medicalRecord)) {
                    followUpTaskEventPublisher.publishFollowUpTaskCreationEvent(
                        new FollowUpTaskCreationEvent(petId, pet.getName(),
                            "Follow-up required for " + medicalRecord.getDiagnosis(),
                            LocalDate.now().plusWeeks(1).toString()));
                }

                log.info("Publishing medical task creation event for Pet ID: {}", petId);
                MedicalTaskCreationEvent event = new MedicalTaskCreationEvent(
                    petId,
                    pet.getName(),
                    pet.getOwnerName(),
                    medicalRecord.getDiagnosis(),
                    "Medical task created for treatment or follow-up"
                );
                medicalTaskEventPublisher.publishMedicalTaskCreationEvent(event);

                return savedRecord;
            })
            .orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    public MedicalRecord updateMedicalRecord(Long recordId, MedicalRecord medicalRecordDetails) {
        log.info("Updating medical record with ID: {}", recordId);
        return medicalRecordRepository.findById(recordId)
            .map(existingRecord -> {
                existingRecord.setDiagnosis(medicalRecordDetails.getDiagnosis());
                existingRecord.setTreatment(medicalRecordDetails.getTreatment());
                existingRecord.setDateOfTreatment(medicalRecordDetails.getDateOfTreatment());

                log.info("Medical record updated for Pet ID: {}", existingRecord.getPet().getId());

                if (isCriticalCondition(existingRecord)) {
                    log.info("Critical condition detected for Pet ID: {}. Publishing emergency task event.", existingRecord.getPet().getId());
                    emergencyTaskEventPublisher.publishEmergencyTaskCreationEvent(
                        new EmergencyTaskCreationEvent(existingRecord.getPet().getId(),
                            existingRecord.getPet().getName(),
                            existingRecord.getDiagnosis(),
                            "Emergency procedure needed for " + existingRecord.getPet().getName()));
                }

                if (requiresFollowUp(existingRecord)) {
                    log.info("Follow-up required for Pet ID: {}. Publishing follow-up task event.", existingRecord.getPet().getId());
                    followUpTaskEventPublisher.publishFollowUpTaskCreationEvent(
                        new FollowUpTaskCreationEvent(existingRecord.getPet().getId(),
                            existingRecord.getPet().getName(),
                            "Follow-up required for " + existingRecord.getDiagnosis(),
                            LocalDate.now().plusWeeks(1).toString()));
                }

                log.info("Publishing medical task creation event for Pet ID: {}", existingRecord.getPet().getId());
                MedicalTaskCreationEvent event = new MedicalTaskCreationEvent(
                    existingRecord.getPet().getId(),
                    existingRecord.getPet().getName(),
                    existingRecord.getPet().getOwnerName(),
                    existingRecord.getDiagnosis(),
                    "Medical task created for updated treatment"
                );
                medicalTaskEventPublisher.publishMedicalTaskCreationEvent(event);

                return medicalRecordRepository.save(existingRecord);
            })
            .orElseThrow(() -> {
                log.error("Medical record not found with ID: {}", recordId);
                return new RuntimeException("Medical record not found");
            });
    }

    public void deleteMedicalRecord(Long recordId) {
        log.info("Deleting medical record with ID: {}", recordId);
        medicalRecordRepository.deleteById(recordId);
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
