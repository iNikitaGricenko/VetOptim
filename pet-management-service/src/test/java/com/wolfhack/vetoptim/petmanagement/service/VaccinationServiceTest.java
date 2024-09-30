package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.petmanagement.event.VaccinationEventPublisher;
import com.wolfhack.vetoptim.petmanagement.model.Pet;
import com.wolfhack.vetoptim.petmanagement.model.Vaccination;
import com.wolfhack.vetoptim.petmanagement.repository.PetRepository;
import com.wolfhack.vetoptim.petmanagement.repository.VaccinationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

class VaccinationServiceTest {

    @Mock
    private VaccinationRepository vaccinationRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private VaccinationEventPublisher vaccinationEventPublisher;

    @InjectMocks
    private VaccinationService vaccinationService;

	private AutoCloseable autoCloseable;

	@BeforeEach
    void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testCreateVaccination_Success() {
        Long petId = 1L;
        Vaccination vaccination = new Vaccination();
        when(petRepository.findById(petId)).thenReturn(Optional.of(new Pet()));

        vaccinationService.createVaccination(petId, vaccination);

        verify(vaccinationRepository).save(vaccination);
        verify(vaccinationEventPublisher, never()).publishVaccinationReminderEvent(any());
    }

    @Test
    void testUpdateVaccination_Success() {
        Long vaccinationId = 1L;
        Vaccination vaccinationDetails = new Vaccination();
        when(vaccinationRepository.findById(vaccinationId)).thenReturn(Optional.of(new Vaccination()));

        vaccinationService.updateVaccination(vaccinationId, vaccinationDetails);

        verify(vaccinationRepository).save(any(Vaccination.class));
    }

    @Test
    void testDeleteVaccination_Success() {
        Long vaccinationId = 1L;
        vaccinationService.deleteVaccination(vaccinationId);

        verify(vaccinationRepository).deleteById(vaccinationId);
    }
}