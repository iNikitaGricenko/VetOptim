package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.petmanagement.model.PetInteraction;
import com.wolfhack.vetoptim.petmanagement.repository.PetInteractionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetInteractionServiceTest {

    @Mock
    private PetInteractionRepository petInteractionRepository;

    @InjectMocks
    private PetInteractionService petInteractionService;

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
    void testGetPetInteractions_Success() {
        Long petId = 1L;
        List<PetInteraction> interactions = List.of(new PetInteraction());
        when(petInteractionRepository.findAllByPetId(petId)).thenReturn(interactions);

        List<PetInteraction> result = petInteractionService.getPetInteractions(petId);

        assertEquals(interactions, result);
        verify(petInteractionRepository).findAllByPetId(petId);
    }

    @Test
    void testLogInteraction_Success() {
        PetInteraction interaction = new PetInteraction();
        when(petInteractionRepository.save(interaction)).thenReturn(interaction);

        PetInteraction result = petInteractionService.logInteraction(interaction);

        assertEquals(interaction, result);
        verify(petInteractionRepository).save(interaction);
    }
}