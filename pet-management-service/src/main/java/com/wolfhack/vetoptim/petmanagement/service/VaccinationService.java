package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.pet.VaccinationRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.VaccinationResponseDTO;
import com.wolfhack.vetoptim.common.event.vaccination.VaccinationReminderEvent;
import com.wolfhack.vetoptim.petmanagement.event.IVaccinationEventPublisher;
import com.wolfhack.vetoptim.petmanagement.mapper.VaccinationMapper;
import com.wolfhack.vetoptim.petmanagement.model.Vaccination;
import com.wolfhack.vetoptim.petmanagement.repository.PetRepository;
import com.wolfhack.vetoptim.petmanagement.repository.VaccinationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VaccinationService implements IVaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final PetRepository petRepository;
    private final VaccinationMapper vaccinationMapper;

    private final IVaccinationEventPublisher vaccinationEventPublisher;

    @Override
    public List<VaccinationResponseDTO> getVaccinationsForPet(Long petId) {
        log.info("Fetching vaccinations for Pet ID: {}", petId);
        return vaccinationRepository.findAllByPetId(petId).stream()
            .map(vaccinationMapper::toDTO)
            .toList();
    }

    @Override
    public VaccinationResponseDTO createVaccination(Long petId, VaccinationRequestDTO vaccinationRequestDTO) {
        log.info("Creating vaccination for Pet ID: {}", petId);
        return petRepository.findById(petId)
            .map(pet -> {
                Vaccination vaccination = vaccinationMapper.toModel(vaccinationRequestDTO);
                vaccination.setPet(pet);
                Vaccination savedVaccination = vaccinationRepository.save(vaccination);

                if (savedVaccination.getNextDueDate().isBefore(LocalDate.now())) {
                    vaccinationEventPublisher.publishVaccinationReminderEvent(
                        new VaccinationReminderEvent(petId, pet.getName(), pet.getOwnerId(), vaccination.getVaccineName(), "overdue"));
                } else if (savedVaccination.getNextDueDate().isBefore(LocalDate.now().plusWeeks(1))) {
                    vaccinationEventPublisher.publishVaccinationReminderEvent(
                        new VaccinationReminderEvent(petId, pet.getName(), pet.getOwnerId(), vaccination.getVaccineName(), "upcoming"));
                }

                return vaccinationMapper.toDTO(savedVaccination);
            })
            .orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    @Override
    public VaccinationResponseDTO updateVaccination(Long vaccinationId, VaccinationRequestDTO vaccinationRequestDTO) {
        log.info("Updating vaccination with ID: {}", vaccinationId);
        return vaccinationRepository.findById(vaccinationId)
            .map(existingVaccination -> {
                vaccinationMapper.updateModelFromDTO(vaccinationRequestDTO, existingVaccination);
                Vaccination updatedVaccination = vaccinationRepository.save(existingVaccination);

                if (updatedVaccination.getNextDueDate().isBefore(LocalDate.now())) {
                    vaccinationEventPublisher.publishVaccinationReminderEvent(
                        new VaccinationReminderEvent(updatedVaccination.getPet().getId(),
                            updatedVaccination.getPet().getName(), updatedVaccination.getPet().getOwnerId(),
                            updatedVaccination.getVaccineName(), "overdue"));
                }

                return vaccinationMapper.toDTO(updatedVaccination);
            })
            .orElseThrow(() -> new RuntimeException("Vaccination not found"));
    }

    @Override
    public void deleteVaccination(Long vaccinationId) {
        log.info("Deleting vaccination with ID: {}", vaccinationId);
        vaccinationRepository.deleteById(vaccinationId);
    }
}
