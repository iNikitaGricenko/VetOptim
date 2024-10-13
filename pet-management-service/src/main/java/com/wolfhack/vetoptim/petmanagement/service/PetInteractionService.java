package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.AppointmentStatus;
import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.common.dto.pet.MedicalRecordDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetInteractionRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetInteractionResponseDTO;
import com.wolfhack.vetoptim.petmanagement.exception.PetNotFoundException;
import com.wolfhack.vetoptim.petmanagement.mapper.PetInteractionMapper;
import com.wolfhack.vetoptim.petmanagement.model.Pet;
import com.wolfhack.vetoptim.petmanagement.model.PetInteraction;
import com.wolfhack.vetoptim.petmanagement.repository.PetInteractionRepository;
import com.wolfhack.vetoptim.petmanagement.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetInteractionService implements IPetInteractionService {

    private final PetInteractionRepository petInteractionRepository;
    private final PetRepository petRepository;
    private final PetInteractionMapper petInteractionMapper;

    private final IMedicalRecordService medicalRecordService;
    private final INotificationService notificationService;

    @Override
    public List<PetInteractionResponseDTO> getPetInteractions(Long petId) {
        log.info("Fetching interactions for Pet ID: {}", petId);
        return petInteractionRepository.findAllByPetId(petId)
            .stream()
            .map(petInteractionMapper::toDTO)
            .toList();
    }

    @Override
    public PetInteractionResponseDTO logInteraction(Long petId, PetInteractionRequestDTO interactionRequest) {
        log.info("Logging new interaction for Pet ID: {}", petId);

        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new PetNotFoundException(petId));

        PetInteraction interaction = new PetInteraction();
        interaction.setInteractionType(interactionRequest.getInteractionType());
        interaction.setDescription(interactionRequest.getDescription());
        interaction.setInteractionDate(interactionRequest.getInteractionDate());
        interaction.setPet(pet);

         PetInteraction savedInteraction = petInteractionRepository.save(interaction);

        if ("Illness".equalsIgnoreCase(interaction.getInteractionType())) {
            log.info("Pet is reported ill. Logging illness in medical record for Pet ID: {}", interaction.getPet().getId());
            medicalRecordService.createMedicalRecord(interaction.getPet().getId(),
                new MedicalRecordDTO(null, "Illness Reported", "Checkup needed", LocalDate.now(), interaction.getPet().getId())
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

        return petInteractionMapper.toDTO(savedInteraction);
    }
}
