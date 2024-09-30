package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.common.dto.PetHealthSummary;
import com.wolfhack.vetoptim.petmanagement.model.Pet;
import com.wolfhack.vetoptim.petmanagement.service.PetHealthAnalyticsService;
import com.wolfhack.vetoptim.petmanagement.service.PetService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PetControllerTest {

    @Mock
    private PetService petService;

    @Mock
    private PetHealthAnalyticsService petHealthAnalyticsService;

    @InjectMocks
    private PetController petController;

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
    void testGetAllPets_Success() {
        List<Pet> pets = List.of(new Pet());
        when(petService.getAllPets()).thenReturn(pets);

        ResponseEntity<List<Pet>> response = petController.getAllPets();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(pets, response.getBody());
    }

    @Test
    void testGetPetById_Success() {
        Long petId = 1L;
        when(petService.getPetById(petId)).thenReturn(Optional.of(new Pet()));

        ResponseEntity<Pet> response = petController.getPetById(petId);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testCreatePet_Success() {
        Pet pet = new Pet();
        when(petService.createPet(pet)).thenReturn(pet);

        ResponseEntity<Pet> response = petController.createPet(pet);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(pet, response.getBody());
    }

    @Test
    void testUpdatePet_Success() {
        Long petId = 1L;
        Pet petDetails = new Pet();
        when(petService.updatePet(petId, petDetails)).thenReturn(petDetails);

        ResponseEntity<Pet> response = petController.updatePet(petId, petDetails);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(petDetails, response.getBody());
    }

    @Test
    void testDeletePet_Success() {
        Long petId = 1L;

        ResponseEntity<Void> response = petController.deletePet(petId);

        assertEquals(204, response.getStatusCode().value());
        verify(petService).deletePet(petId);
    }

    @Test
    void testGetPetHealthSummary_Success() {
        Long petId = 1L;
        PetHealthSummary summary = new PetHealthSummary();
        when(petHealthAnalyticsService.getPetHealthSummary(petId)).thenReturn(summary);

        ResponseEntity<PetHealthSummary> response = petController.getPetHealthSummary(petId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(summary, response.getBody());
    }
}