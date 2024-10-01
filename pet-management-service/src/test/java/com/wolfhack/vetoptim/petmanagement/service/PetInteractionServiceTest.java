package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import com.wolfhack.vetoptim.petmanagement.model.Pet;
import com.wolfhack.vetoptim.petmanagement.model.PetInteraction;
import com.wolfhack.vetoptim.petmanagement.repository.PetInteractionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetInteractionServiceTest {

    @Mock
    private PetInteractionRepository petInteractionRepository;

    @Mock
    private MedicalRecordService medicalRecordService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PetInteractionService petInteractionService;

    private Pet pet;
    private PetInteraction interaction;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");

        interaction = new PetInteraction();
        interaction.setPet(pet);
        interaction.setInteractionType("Illness");
    }

    @Test
    void testGetPetInteractions_Success() {
        Long petId = 1L;
        List<PetInteraction> interactions = List.of(interaction);

        when(petInteractionRepository.findAllByPetId(petId)).thenReturn(interactions);

        List<PetInteraction> result = petInteractionService.getPetInteractions(petId);

        assertEquals(1, result.size());
        assertEquals("Illness", result.getFirst().getInteractionType());

        verify(petInteractionRepository).findAllByPetId(petId);
    }

    @Test
    void testLogInteraction_Illness_Success() {
        interaction.setInteractionType("Illness");

        when(petInteractionRepository.save(interaction)).thenReturn(interaction);

        PetInteraction savedInteraction = petInteractionService.logInteraction(interaction);

        assertEquals("Illness", savedInteraction.getInteractionType());

        verify(petInteractionRepository).save(interaction);

        verify(medicalRecordService).createMedicalRecord(eq(pet.getId()), any(MedicalRecord.class));
    }

    @Test
    void testLogInteraction_AggressiveBehavior_Success() {
        interaction.setInteractionType("Aggressive Behavior");

        when(petInteractionRepository.save(interaction)).thenReturn(interaction);

        PetInteraction savedInteraction = petInteractionService.logInteraction(interaction);

        assertEquals("Aggressive Behavior", savedInteraction.getInteractionType());

        verify(petInteractionRepository).save(interaction);

        verify(notificationService).notifyOwnerOfAppointment(any(AppointmentDTO.class));
    }

    @Test
    void testLogInteraction_NonCriticalInteraction_Success() {
        interaction.setInteractionType("Playing");

        when(petInteractionRepository.save(interaction)).thenReturn(interaction);

        PetInteraction savedInteraction = petInteractionService.logInteraction(interaction);

        assertEquals("Playing", savedInteraction.getInteractionType());

        verify(petInteractionRepository).save(interaction);

        verifyNoInteractions(medicalRecordService);
        verifyNoInteractions(notificationService);
    }
}