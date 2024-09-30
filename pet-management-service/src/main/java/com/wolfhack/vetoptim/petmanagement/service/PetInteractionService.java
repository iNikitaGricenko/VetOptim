package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.AppointmentStatus;
import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import com.wolfhack.vetoptim.petmanagement.model.PetInteraction;
import com.wolfhack.vetoptim.petmanagement.repository.PetInteractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetInteractionService {

    private final PetInteractionRepository petInteractionRepository;
    private final MedicalRecordService medicalRecordService;
    private final NotificationService notificationService;

    public List<PetInteraction> getPetInteractions(Long petId) {
        log.info("Fetching interactions for Pet ID: {}", petId);
        return petInteractionRepository.findAllByPetId(petId);
    }

    public PetInteraction logInteraction(PetInteraction interaction) {
        log.info("Logging new interaction for Pet ID: {}", interaction.getPet().getId());
         PetInteraction savedInteraction = petInteractionRepository.save(interaction);

        if ("Illness".equalsIgnoreCase(interaction.getInteractionType())) {
            log.info("Pet is reported ill. Logging illness in medical record for Pet ID: {}", interaction.getPet().getId());
            medicalRecordService.createMedicalRecord(interaction.getPet().getId(),
                new MedicalRecord(null, "Illness Reported", "Checkup needed", LocalDate.now(), interaction.getPet())
            );
        }

        if ("Aggressive Behavior".equalsIgnoreCase(interaction.getInteractionType())) {
            log.info("Aggressive behavior detected. Notifying staff.");
            notificationService.notifyOwnerOfAppointment(new AppointmentDTO(
                null, interaction.getPet().getId(), interaction.getPet().getName(),
                "Veterinarian", "Behavior consultation needed",
                LocalDateTime.now(), false, null, AppointmentStatus.SCHEDULED, null, null
            ));
        }

        return savedInteraction;
    }
}
