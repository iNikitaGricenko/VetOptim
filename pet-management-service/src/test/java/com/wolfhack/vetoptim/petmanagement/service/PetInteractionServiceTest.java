package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.common.dto.pet.MedicalRecordDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetInteractionRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetInteractionResponseDTO;
import com.wolfhack.vetoptim.petmanagement.mapper.PetInteractionMapper;
import com.wolfhack.vetoptim.petmanagement.mapper.PetInteractionMapperImpl;
import com.wolfhack.vetoptim.petmanagement.model.Pet;
import com.wolfhack.vetoptim.petmanagement.model.PetInteraction;
import com.wolfhack.vetoptim.petmanagement.repository.PetInteractionRepository;
import com.wolfhack.vetoptim.petmanagement.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetInteractionServiceTest {

    @Mock
    private PetInteractionRepository petInteractionRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private MedicalRecordService medicalRecordService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PetInteractionMapper petInteractionMapper;

    @InjectMocks
    private PetInteractionService petInteractionService;

    private PetInteractionRequestDTO requestDTO;
    private PetInteractionResponseDTO responseDTO;
    private PetInteraction interaction;
    private Pet pet;

    @BeforeEach
    void setUp() {
        requestDTO = new PetInteractionRequestDTO("Illness", "Pet feels sick", LocalDateTime.now());
        interaction = new PetInteraction();

        pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setOwnerId(1L);

        interaction.setInteractionType("Illness");

        responseDTO = new PetInteractionResponseDTO(1L, "Illness", "Pet feels sick", LocalDateTime.now(), 1L);
    }

    @Test
    void testGetPetInteractions_Success() {
        Long petId = 1L;
        List<PetInteraction> interactions = List.of(interaction);

        when(petInteractionRepository.findAllByPetId(petId)).thenReturn(interactions);
        when(petInteractionMapper.toDTO(any(PetInteraction.class))).thenReturn(responseDTO);

        List<PetInteractionResponseDTO> result = petInteractionService.getPetInteractions(petId);

        assertEquals(1, result.size());
        assertEquals("Illness", result.getFirst().getInteractionType());

        verify(petInteractionRepository).findAllByPetId(petId);
        verify(petInteractionMapper, times(1)).toDTO(any(PetInteraction.class));
    }

    @Test
    void testLogInteraction_Illness_Success() {
        Long petId = 1L;

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petInteractionRepository.save(any(PetInteraction.class))).thenReturn(interaction);
        when(petInteractionMapper.toDTO(any(PetInteraction.class))).thenReturn(responseDTO);

        PetInteractionResponseDTO savedInteraction = petInteractionService.logInteraction(petId, requestDTO);

        assertEquals("Illness", savedInteraction.getInteractionType());
        verify(petInteractionRepository).save(any(PetInteraction.class));
        verify(medicalRecordService).createMedicalRecord(eq(1L), any(MedicalRecordDTO.class));
        verify(petInteractionMapper, times(1)).toDTO(any(PetInteraction.class));
    }

    @Test
    void testLogInteraction_AggressiveBehavior_Success() {
        requestDTO.setInteractionType("Aggressive Behavior");
        interaction.setInteractionType("Aggressive Behavior");
        responseDTO.setInteractionType("Aggressive Behavior");

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petInteractionRepository.save(any(PetInteraction.class))).thenReturn(interaction);
        when(petInteractionMapper.toDTO(any(PetInteraction.class))).thenReturn(responseDTO);

        PetInteractionResponseDTO savedInteraction = petInteractionService.logInteraction(1L, requestDTO);

        assertEquals("Aggressive Behavior", savedInteraction.getInteractionType());
        verify(petInteractionRepository).save(any(PetInteraction.class));
        verify(notificationService).notifyOwnerOfAppointment(any(AppointmentDTO.class));
    }

    @Test
    void testLogInteraction_NonCriticalInteraction_Success() {
        requestDTO.setInteractionType("Playing");
        interaction.setInteractionType("Playing");
        responseDTO.setInteractionType("Playing");

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petInteractionRepository.save(any(PetInteraction.class))).thenReturn(interaction);
        when(petInteractionMapper.toDTO(any(PetInteraction.class))).thenReturn(responseDTO);

        PetInteractionResponseDTO savedInteraction = petInteractionService.logInteraction(1L, requestDTO);

        assertEquals("Playing", savedInteraction.getInteractionType());
        verify(petInteractionRepository).save(any(PetInteraction.class));
        verifyNoInteractions(medicalRecordService);
        verifyNoInteractions(notificationService);
    }
}