package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.event.vaccination.VaccinationReminderEvent;
import com.wolfhack.vetoptim.petmanagement.event.VaccinationEventPublisher;
import com.wolfhack.vetoptim.petmanagement.model.Pet;
import com.wolfhack.vetoptim.petmanagement.model.Vaccination;
import com.wolfhack.vetoptim.petmanagement.repository.PetRepository;
import com.wolfhack.vetoptim.petmanagement.repository.VaccinationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaccinationServiceTest {

    @Mock
    private VaccinationRepository vaccinationRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private VaccinationEventPublisher vaccinationEventPublisher;

    @InjectMocks
    private VaccinationService vaccinationService;

    private Pet pet;
    private Vaccination vaccination;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setOwnerId(100L);

        vaccination = new Vaccination();
        vaccination.setPet(pet);
        vaccination.setVaccineName("Rabies");
        vaccination.setVaccinationDate(LocalDate.now());
        vaccination.setNextDueDate(LocalDate.now().plusMonths(6));
    }

    @Test
    void testGetVaccinationsForPet_Success() {
        Long petId = 1L;
        List<Vaccination> vaccinations = List.of(vaccination);

        when(vaccinationRepository.findAllByPetId(petId)).thenReturn(vaccinations);

        List<Vaccination> result = vaccinationService.getVaccinationsForPet(petId);

        assertEquals(1, result.size());
        assertEquals("Rabies", result.get(0).getVaccineName());

        verify(vaccinationRepository).findAllByPetId(petId);
    }

    @Test
    void testCreateVaccination_Success() {
        Long petId = 1L;

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(vaccinationRepository.save(vaccination)).thenReturn(vaccination);

        Vaccination savedVaccination = vaccinationService.createVaccination(petId, vaccination);

        assertEquals("Rabies", savedVaccination.getVaccineName());
        verify(vaccinationRepository).save(vaccination);
        verify(vaccinationEventPublisher, never()).publishVaccinationReminderEvent(any());

        verifyNoMoreInteractions(vaccinationEventPublisher);
    }

    @Test
    void testCreateVaccination_UpcomingReminder() {
        Long petId = 1L;
        vaccination.setNextDueDate(LocalDate.now().plusDays(3));

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(vaccinationRepository.save(vaccination)).thenReturn(vaccination);

        vaccinationService.createVaccination(petId, vaccination);

        verify(vaccinationEventPublisher).publishVaccinationReminderEvent(any(VaccinationReminderEvent.class));
    }

    @Test
    void testCreateVaccination_OverdueReminder() {
        Long petId = 1L;
        vaccination.setNextDueDate(LocalDate.now().minusDays(1));

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(vaccinationRepository.save(vaccination)).thenReturn(vaccination);

        vaccinationService.createVaccination(petId, vaccination);

        verify(vaccinationEventPublisher).publishVaccinationReminderEvent(any(VaccinationReminderEvent.class));
    }

    @Test
    void testCreateVaccination_PetNotFound() {
        Long petId = 1L;

        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vaccinationService.createVaccination(petId, vaccination);
        });

        assertEquals("Pet not found", exception.getMessage());

        verify(petRepository).findById(petId);
        verifyNoMoreInteractions(vaccinationRepository, vaccinationEventPublisher);
    }

    @Test
    void testUpdateVaccination_Success() {
        Long vaccinationId = 1L;
        Vaccination updatedDetails = new Vaccination();
        updatedDetails.setVaccineName("Distemper");
        updatedDetails.setVaccinationDate(LocalDate.now().plusMonths(1));
        updatedDetails.setNextDueDate(LocalDate.now().plusMonths(12));

        when(vaccinationRepository.findById(vaccinationId)).thenReturn(Optional.of(vaccination));
        when(vaccinationRepository.save(vaccination)).thenReturn(vaccination);

        Vaccination updatedVaccination = vaccinationService.updateVaccination(vaccinationId, updatedDetails);

        assertEquals("Distemper", updatedVaccination.getVaccineName());
        verify(vaccinationRepository).save(any(Vaccination.class));

        verifyNoMoreInteractions(vaccinationEventPublisher);
    }

    @Test
    void testUpdateVaccination_OverdueReminder() {
        Long vaccinationId = 1L;
        Vaccination updatedDetails = new Vaccination();
        updatedDetails.setVaccineName("Distemper");
        updatedDetails.setVaccinationDate(LocalDate.now().minusMonths(1));
        updatedDetails.setNextDueDate(LocalDate.now().minusDays(1));

        when(vaccinationRepository.findById(vaccinationId)).thenReturn(Optional.of(vaccination));
        when(vaccinationRepository.save(vaccination)).thenReturn(vaccination);

        vaccinationService.updateVaccination(vaccinationId, updatedDetails);

        verify(vaccinationEventPublisher).publishVaccinationReminderEvent(any(VaccinationReminderEvent.class));
    }

    @Test
    void testDeleteVaccination_Success() {
        Long vaccinationId = 1L;

        vaccinationService.deleteVaccination(vaccinationId);

        verify(vaccinationRepository).deleteById(vaccinationId);
    }
}