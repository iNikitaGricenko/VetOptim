package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetDTO;
import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
import com.wolfhack.vetoptim.common.event.pet.PetCreatedEvent;
import com.wolfhack.vetoptim.common.event.pet.PetDeletedEvent;
import com.wolfhack.vetoptim.common.event.pet.PetUpdatedEvent;
import com.wolfhack.vetoptim.petmanagement.client.OwnerClient;
import com.wolfhack.vetoptim.petmanagement.event.IAppointmentTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.event.IPetEventPublisher;
import com.wolfhack.vetoptim.petmanagement.exception.OwnerNotFoundException;
import com.wolfhack.vetoptim.petmanagement.exception.PetNotFoundException;
import com.wolfhack.vetoptim.petmanagement.mapper.PetMapper;
import com.wolfhack.vetoptim.petmanagement.model.Pet;
import com.wolfhack.vetoptim.petmanagement.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PetService implements IPetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;
    private final OwnerClient ownerClient;
    private final IPetEventPublisher petEventPublisher;
    private final IAppointmentTaskEventPublisher taskEventPublisher;

    @Override
    public List<PetDTO> getAllPets() {
        log.info("Fetching all pets.");
        return petRepository.findAll().stream()
            .map(petMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<PetDTO> getPetById(Long id) {
        log.info("Fetching pet with ID: {}", id);
        return petRepository.findById(id)
            .map(petMapper::toDTO)
            .or(() -> {
                throw new PetNotFoundException(id);
            });
    }

    @Override
    public List<PetDTO> getAllPetsByOwnerId(Long ownerId) {
        log.info("Fetching pets with ownerID: {}", ownerId);
        return petRepository.findAllByOwnerId(ownerId).stream()
            .map(petMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public PetDTO createPet(PetDTO petDTO) {
        log.info("Creating pet with name: {}", petDTO.getName());

        Long ownerId = petDTO.getOwnerId();
        if (!ownerClient.ownerExists(ownerId)) {
            throw new OwnerNotFoundException(ownerId);
        }

        Pet pet = petMapper.toModel(petDTO);
        Pet savedPet = petRepository.save(pet);
        log.debug("Pet created with ID: {}", savedPet.getId());

        PetCreatedEvent event = new PetCreatedEvent(savedPet.getId(), savedPet.getName(), savedPet.getSpecies(), savedPet.getBreed(), ownerId);
        petEventPublisher.publishPetCreatedEvent(event);
        log.info("Published pet created event for Pet ID: {}", savedPet.getId());

        return petMapper.toDTO(savedPet);
    }

    @Override
    public PetDTO updatePet(Long id, PetDTO petDetails) {
        log.info("Updating pet with ID: {}", id);
        return petRepository.findById(id)
            .map(existingPet -> {
                petMapper.updatePetFromDTO(petDetails, existingPet);
                Pet updatedPet = petRepository.save(existingPet);

                PetUpdatedEvent event = new PetUpdatedEvent(updatedPet.getId(), updatedPet.getName(), updatedPet.getSpecies(), updatedPet.getBreed(), updatedPet.getOwnerId());
                petEventPublisher.publishPetUpdatedEvent(event);
                log.info("Published pet updated event for Pet ID: {}", updatedPet.getId());

                return petMapper.toDTO(updatedPet);
            })
            .orElseThrow(() -> new PetNotFoundException(id));
    }

    @Override
    public void updateOwnerInfoForPets(Long ownerId, String ownerName) {
        log.info("Updating owner information for all pets of owner ID: {}", ownerId);
        List<Pet> pets = petRepository.findAllByOwnerId(ownerId);

        for (Pet pet : pets) {
            pet.setOwnerName(ownerName);
            petRepository.save(pet);

            PetUpdatedEvent event = new PetUpdatedEvent(pet.getId(), pet.getName(), pet.getSpecies(), pet.getBreed(), ownerId);
            petEventPublisher.publishPetUpdatedEvent(event);
            log.info("Updated and published pet event for Pet ID: {}", pet.getId());
        }
    }

    @Override
    public void deletePet(Long id) {
        log.info("Deleting pet with ID: {}", id);
        petRepository.findById(id).ifPresentOrElse(pet -> {
            petRepository.deleteById(id);
            PetDeletedEvent event = new PetDeletedEvent(pet.getId());
            petEventPublisher.publishPetDeletedEvent(event);
            log.info("Published pet deleted event for Pet ID: {}", pet.getId());
        }, () -> {
            throw new PetNotFoundException(id);
        });
    }

    @Override
    public void handleAppointmentCreated(AppointmentDTO appointmentDTO) {
        log.info("Handling appointment creation for Pet ID: {}", appointmentDTO.getPetId());
        petRepository.findById(appointmentDTO.getPetId()).ifPresent(pet -> {
            AppointmentTaskCreationEvent taskEvent = new AppointmentTaskCreationEvent(
                appointmentDTO.getId(),
                pet.getId(),
                pet.getName(),
                appointmentDTO.getVeterinarianName(),
                "Checkup",
                "Checkup scheduled for pet " + pet.getName()
            );
            taskEventPublisher.publishAppointmentTaskCreationEvent(taskEvent);
            log.info("Published appointment task creation event for Appointment ID: {}", appointmentDTO.getId());
        });
    }

    @Override
    public void handleAppointmentUpdated(AppointmentDTO appointmentDTO) {
        log.info("Handling appointment update for Pet ID: {}", appointmentDTO.getPetId());
        petRepository.findById(appointmentDTO.getPetId()).ifPresent(pet -> {
            AppointmentTaskCreationEvent taskEvent = new AppointmentTaskCreationEvent(
                appointmentDTO.getId(),
                pet.getId(),
                pet.getName(),
                appointmentDTO.getVeterinarianName(),
                "Surgery",
                "Surgery updated for pet " + pet.getName()
            );
            taskEventPublisher.publishAppointmentTaskCreationEvent(taskEvent);
            log.info("Published appointment task update event for Appointment ID: {}", appointmentDTO.getId());
        });
    }


}
