package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.petmanagement.model.Vaccination;
import com.wolfhack.vetoptim.petmanagement.service.VaccinationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class VaccinationControllerTest {

    @Mock
    private VaccinationService vaccinationService;

    @InjectMocks
    private VaccinationController vaccinationController;

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
    void testGetVaccinationsForPet_Success() {
        Long petId = 1L;
        List<Vaccination> vaccinations = List.of(new Vaccination());
        when(vaccinationService.getVaccinationsForPet(petId)).thenReturn(vaccinations);

        ResponseEntity<List<Vaccination>> response = vaccinationController.getVaccinationsForPet(petId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(vaccinations, response.getBody());
    }

    @Test
    void testCreateVaccination_Success() {
        Long petId = 1L;
        Vaccination vaccination = new Vaccination();
        when(vaccinationService.createVaccination(petId, vaccination)).thenReturn(vaccination);

        ResponseEntity<Vaccination> response = vaccinationController.createVaccination(petId, vaccination);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(vaccination, response.getBody());
    }

    @Test
    void testUpdateVaccination_Success() {
        Long vaccinationId = 1L;
        Vaccination vaccination = new Vaccination();
        when(vaccinationService.updateVaccination(vaccinationId, vaccination)).thenReturn(vaccination);

        ResponseEntity<Vaccination> response = vaccinationController.updateVaccination(vaccinationId, vaccination);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(vaccination, response.getBody());
    }

    @Test
    void testDeleteVaccination_Success() {
        Long vaccinationId = 1L;

        ResponseEntity<Void> response = vaccinationController.deleteVaccination(vaccinationId);

        assertEquals(204, response.getStatusCode().value());
        verify(vaccinationService).deleteVaccination(vaccinationId);
    }
}