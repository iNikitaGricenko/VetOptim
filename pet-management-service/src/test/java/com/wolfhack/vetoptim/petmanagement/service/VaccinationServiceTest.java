package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.pet.VaccinationRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.VaccinationResponseDTO;
import com.wolfhack.vetoptim.common.event.vaccination.VaccinationReminderEvent;
import com.wolfhack.vetoptim.petmanagement.event.IVaccinationEventPublisher;
import com.wolfhack.vetoptim.petmanagement.mapper.VaccinationMapper;
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
    private IVaccinationEventPublisher vaccinationEventPublisher;

    @Mock
    private VaccinationMapper vaccinationMapper;

    @InjectMocks
    private IVaccinationService IVaccinationService;

    private Pet pet;
    private Vaccination vaccination;
    private VaccinationResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setOwnerId(100L);

        vaccination = new Vaccination();
        vaccination.setId(1L);
        vaccination.setPet(pet);
        vaccination.setVaccineName("Rabies");
        vaccination.setVaccinationDate(LocalDate.now());
        vaccination.setNextDueDate(LocalDate.now().plusMonths(6));

        responseDTO = new VaccinationResponseDTO(1L, "Rabies", LocalDate.now(), LocalDate.now().plusMonths(6), 1L);
    }

    @Test
    void testGetVaccinationsForPet_Success() {
        Long petId = 1L;
        List<Vaccination> vaccinations = List.of(vaccination);

        when(vaccinationRepository.findAllByPetId(petId)).thenReturn(vaccinations);
        when(vaccinationMapper.toDTO(any(Vaccination.class))).thenReturn(responseDTO);

        List<VaccinationResponseDTO> result = IVaccinationService.getVaccinationsForPet(petId);

        assertEquals(1, result.size());
        assertEquals("Rabies", result.getFirst().getVaccineName());

        verify(vaccinationRepository).findAllByPetId(petId);
    }

    @Test
    void testCreateVaccination_Success() {
        Long petId = 1L;
        VaccinationRequestDTO requestDTO = new VaccinationRequestDTO("Rabies", LocalDate.now(), LocalDate.now().plusMonths(6));

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(vaccinationMapper.toModel(any(VaccinationRequestDTO.class))).thenReturn(vaccination);
        when(vaccinationRepository.save(vaccination)).thenReturn(vaccination);
        when(vaccinationMapper.toDTO(any(Vaccination.class))).thenReturn(responseDTO);

        VaccinationResponseDTO savedVaccination = IVaccinationService.createVaccination(petId, requestDTO);

        assertEquals("Rabies", savedVaccination.getVaccineName());
        verify(vaccinationRepository).save(vaccination);
        verify(vaccinationEventPublisher, never()).publishVaccinationReminderEvent(any());
    }

    @Test
    void testCreateVaccination_UpcomingReminder() {
        Long petId = 1L;
        VaccinationRequestDTO requestDTO = new VaccinationRequestDTO("Rabies", LocalDate.now(), LocalDate.now().plusDays(3));
        vaccination.setNextDueDate(LocalDate.now().plusDays(3));

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(vaccinationMapper.toModel(any(VaccinationRequestDTO.class))).thenReturn(vaccination);
        when(vaccinationRepository.save(vaccination)).thenReturn(vaccination);
        when(vaccinationMapper.toDTO(any(Vaccination.class))).thenReturn(responseDTO);

        IVaccinationService.createVaccination(petId, requestDTO);

        verify(vaccinationEventPublisher).publishVaccinationReminderEvent(any(VaccinationReminderEvent.class));
    }

    @Test
    void testCreateVaccination_OverdueReminder() {
        Long petId = 1L;
        VaccinationRequestDTO requestDTO = new VaccinationRequestDTO("Rabies", LocalDate.now(), LocalDate.now().minusDays(1));
        vaccination.setNextDueDate(LocalDate.now().minusDays(1));

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(vaccinationMapper.toModel(any(VaccinationRequestDTO.class))).thenReturn(vaccination);
        when(vaccinationRepository.save(vaccination)).thenReturn(vaccination);
        when(vaccinationMapper.toDTO(any(Vaccination.class))).thenReturn(responseDTO);

        IVaccinationService.createVaccination(petId, requestDTO);

        verify(vaccinationEventPublisher).publishVaccinationReminderEvent(any(VaccinationReminderEvent.class));
    }

    @Test
    void testCreateVaccination_PetNotFound() {
        Long petId = 1L;
        VaccinationRequestDTO requestDTO = new VaccinationRequestDTO("Rabies", LocalDate.now(), LocalDate.now().plusMonths(6));

        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> IVaccinationService.createVaccination(petId, requestDTO));

        assertEquals("Pet not found", exception.getMessage());
        verify(petRepository).findById(petId);
        verifyNoInteractions(vaccinationRepository, vaccinationEventPublisher);
    }

    @Test
    void testUpdateVaccination_Success() {
        Long vaccinationId = 1L;
        VaccinationRequestDTO updatedDetails = new VaccinationRequestDTO("Distemper", LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(12));
        responseDTO.setVaccineName("Distemper");

        when(vaccinationRepository.findById(vaccinationId)).thenReturn(Optional.of(vaccination));
        when(vaccinationRepository.save(any(Vaccination.class))).thenReturn(vaccination);
        when(vaccinationMapper.toDTO(any(Vaccination.class))).thenReturn(responseDTO);

        VaccinationResponseDTO updatedVaccination = IVaccinationService.updateVaccination(vaccinationId, updatedDetails);

        assertEquals("Distemper", updatedVaccination.getVaccineName());
        verify(vaccinationRepository).save(any(Vaccination.class));
        verifyNoInteractions(vaccinationEventPublisher);
    }

    @Test
    void testUpdateVaccination_OverdueReminder() {
        Long vaccinationId = 1L;
        VaccinationRequestDTO updatedDetails = new VaccinationRequestDTO("Distemper", LocalDate.now().minusMonths(1), LocalDate.now().minusDays(1));

        vaccination.setNextDueDate(LocalDate.now().minusDays(1));

        when(vaccinationRepository.findById(vaccinationId)).thenReturn(Optional.of(vaccination));
        when(vaccinationRepository.save(any(Vaccination.class))).thenReturn(vaccination);
        when(vaccinationMapper.toDTO(any(Vaccination.class))).thenReturn(responseDTO);

        IVaccinationService.updateVaccination(vaccinationId, updatedDetails);

        verify(vaccinationEventPublisher).publishVaccinationReminderEvent(any(VaccinationReminderEvent.class));
    }

    @Test
    void testDeleteVaccination_Success() {
        Long vaccinationId = 1L;

        IVaccinationService.deleteVaccination(vaccinationId);

        verify(vaccinationRepository).deleteById(vaccinationId);
    }
}