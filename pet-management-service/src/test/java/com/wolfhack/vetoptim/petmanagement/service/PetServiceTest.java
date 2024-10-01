package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.petmanagement.client.OwnerClient;
import com.wolfhack.vetoptim.petmanagement.event.AppointmentTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.event.PetEventPublisher;
import com.wolfhack.vetoptim.petmanagement.mapper.PetMapper;
import com.wolfhack.vetoptim.petmanagement.model.Pet;
import com.wolfhack.vetoptim.petmanagement.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetMapper petMapper;

    @Mock
    private OwnerClient ownerClient;

    @Mock
    private PetEventPublisher petEventPublisher;

    @Mock
    private AppointmentTaskEventPublisher taskEventPublisher;

    @InjectMocks
    private PetService petService;

    private Pet pet;
    private AppointmentDTO appointmentDTO;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setOwnerId(1L);

        appointmentDTO = new AppointmentDTO();
        appointmentDTO.setId(1L);
        appointmentDTO.setPetId(pet.getId());
        appointmentDTO.setVeterinarianName("Dr. Smith");
    }

    @Test
    void testGetAllPets() {
        when(petRepository.findAll()).thenReturn(List.of(pet));
        List<Pet> pets = petService.getAllPets();
        assertEquals(1, pets.size());
        verify(petRepository).findAll();
    }

    @Test
    void testGetPetById_Success() {
        Long petId = 1L;
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));

        Optional<Pet> foundPet = petService.getPetById(petId);

        assertTrue(foundPet.isPresent());
        assertEquals(petId, foundPet.get().getId());
        verify(petRepository).findById(petId);
    }

    @Test
    void testGetAllPetsByOwnerId() {
        Long ownerId = 1L;
        when(petRepository.findAllByOwnerId(ownerId)).thenReturn(List.of(pet));

        List<Pet> pets = petService.getAllPetsByOwnerId(ownerId);

        assertEquals(1, pets.size());
        verify(petRepository).findAllByOwnerId(ownerId);
    }

    @Test
    void testCreatePet_Success() {
        when(ownerClient.ownerExists(pet.getOwnerId())).thenReturn(true);
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        Pet savedPet = petService.createPet(pet);

        verify(petRepository).save(pet);
        verify(petEventPublisher).publishPetCreatedEvent(any());
        assertNotNull(savedPet);
    }

    @Test
    void testCreatePet_Failure_OwnerNotFound() {
        when(ownerClient.ownerExists(pet.getOwnerId())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> petService.createPet(pet));

        assertEquals("Owner not found with ID: " + pet.getOwnerId(), exception.getMessage());
        verify(petRepository, never()).save(any());
        verify(petEventPublisher, never()).publishPetCreatedEvent(any());
    }

    @Test
    void testUpdatePet_Success() {
        Long petId = 1L;
        Pet updatedPet = new Pet();
        updatedPet.setId(petId);
        updatedPet.setName("Max");

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenReturn(updatedPet);

        Pet result = petService.updatePet(petId, updatedPet);

        verify(petRepository).save(pet);
        verify(petEventPublisher).publishPetUpdatedEvent(any());
        assertEquals("Max", result.getName());
    }

    @Test
    void testUpdatePet_Failure_PetNotFound() {
        Long petId = 1L;
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> petService.updatePet(petId, pet));

        assertEquals("Pet not found", exception.getMessage());
        verify(petRepository, never()).save(any());
        verify(petEventPublisher, never()).publishPetUpdatedEvent(any());
    }

    @Test
    void testUpdateOwnerInfoForPets() {
        Long ownerId = 1L;
        String ownerName = "John Updated";
        List<Pet> pets = List.of(pet);

        when(petRepository.findAllByOwnerId(ownerId)).thenReturn(pets);

        petService.updateOwnerInfoForPets(ownerId, ownerName);

        pets.forEach(p -> assertEquals(ownerName, p.getOwnerName()));
        verify(petRepository, times(1)).save(pet);
        verify(petEventPublisher).publishPetUpdatedEvent(any());
    }

    @Test
    void testDeletePet_Success() {
        Long petId = 1L;
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));

        petService.deletePet(petId);

        verify(petRepository).deleteById(petId);
        verify(petEventPublisher).publishPetDeletedEvent(any());
    }

    @Test
    void testHandleAppointmentCreated_Success() {
        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));

        petService.handleAppointmentCreated(appointmentDTO);

        verify(taskEventPublisher).publishAppointmentTaskCreationEvent(any());
    }

    @Test
    void testHandleAppointmentUpdated_Success() {
        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));

        petService.handleAppointmentUpdated(appointmentDTO);

        verify(taskEventPublisher).publishAppointmentTaskCreationEvent(any());
    }
}