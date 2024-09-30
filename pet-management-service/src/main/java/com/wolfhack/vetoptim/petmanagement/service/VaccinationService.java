package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.event.vaccination.VaccinationReminderEvent;
import com.wolfhack.vetoptim.petmanagement.event.VaccinationEventPublisher;
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
public class VaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final PetRepository petRepository;
    private final VaccinationEventPublisher vaccinationEventPublisher;

    public List<Vaccination> getVaccinationsForPet(Long petId) {
        log.info("Fetching vaccinations for Pet ID: {}", petId);
        return vaccinationRepository.findAllByPetId(petId);
    }

    public Vaccination createVaccination(Long petId, Vaccination vaccination) {
        log.info("Creating vaccination for Pet ID: {}", petId);
        return petRepository.findById(petId)
            .map(pet -> {
                vaccination.setPet(pet);
                Vaccination savedVaccination = vaccinationRepository.save(vaccination);
                log.debug("Vaccination created with ID: {} for Pet ID: {}", savedVaccination.getId(), petId);

                if (vaccination.getNextDueDate().isBefore(LocalDate.now())) {
                    log.info("Vaccination is overdue for Pet ID: {}. Publishing reminder event.", petId);
                    vaccinationEventPublisher.publishVaccinationReminderEvent(
                        new VaccinationReminderEvent(petId, pet.getName(), pet.getOwnerId(),
                            vaccination.getVaccineName(), "overdue"));
                } else if (vaccination.getNextDueDate().isBefore(LocalDate.now().plusWeeks(1))) {
                    log.info("Vaccination is upcoming for Pet ID: {}. Publishing reminder event.", petId);
                    vaccinationEventPublisher.publishVaccinationReminderEvent(
                        new VaccinationReminderEvent(petId, pet.getName(), pet.getOwnerId(),
                            vaccination.getVaccineName(), "upcoming"));
                }

                return savedVaccination;
            })
            .orElseThrow(() -> {
                log.error("Pet not found with ID: {}", petId);
                return new RuntimeException("Pet not found");
            });
    }

    public Vaccination updateVaccination(Long vaccinationId, Vaccination vaccinationDetails) {
        log.info("Updating vaccination with ID: {}", vaccinationId);
        return vaccinationRepository.findById(vaccinationId)
            .map(existingVaccination -> {
                existingVaccination.setVaccineName(vaccinationDetails.getVaccineName());
                existingVaccination.setVaccinationDate(vaccinationDetails.getVaccinationDate());
                existingVaccination.setNextDueDate(vaccinationDetails.getNextDueDate());

                log.info("Vaccination updated with ID: {}", existingVaccination.getId());

                if (existingVaccination.getNextDueDate().isBefore(LocalDate.now())) {
                    log.info("Vaccination is overdue for Pet ID: {}. Publishing reminder event.", existingVaccination.getPet().getId());
                    vaccinationEventPublisher.publishVaccinationReminderEvent(
                        new VaccinationReminderEvent(existingVaccination.getPet().getId(),
                            existingVaccination.getPet().getName(),
                            existingVaccination.getPet().getOwnerId(),
                            existingVaccination.getVaccineName(), "overdue"));
                } else if (existingVaccination.getNextDueDate().isBefore(LocalDate.now().plusWeeks(1))) {
                    log.info("Vaccination is upcoming for Pet ID: {}. Publishing reminder event.", existingVaccination.getPet().getId());
                    vaccinationEventPublisher.publishVaccinationReminderEvent(
                        new VaccinationReminderEvent(existingVaccination.getPet().getId(),
                            existingVaccination.getPet().getName(),
                            existingVaccination.getPet().getOwnerId(),
                            existingVaccination.getVaccineName(), "upcoming"));
                }

                return vaccinationRepository.save(existingVaccination);
            })
            .orElseThrow(() -> {
                log.error("Vaccination not found with ID: {}", vaccinationId);
                return new RuntimeException("Vaccination not found");
            });
    }

    public void deleteVaccination(Long vaccinationId) {
        log.info("Deleting vaccination with ID: {}", vaccinationId);
        vaccinationRepository.deleteById(vaccinationId);
    }
}
