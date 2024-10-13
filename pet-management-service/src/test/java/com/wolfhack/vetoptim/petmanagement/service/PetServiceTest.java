package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetDTO;
import com.wolfhack.vetoptim.petmanagement.client.OwnerClient;
import com.wolfhack.vetoptim.petmanagement.event.IAppointmentTaskEventPublisher;
import com.wolfhack.vetoptim.petmanagement.event.IPetEventPublisher;
import com.wolfhack.vetoptim.petmanagement.exception.OwnerNotFoundException;
import com.wolfhack.vetoptim.petmanagement.exception.PetNotFoundException;
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
    private IPetEventPublisher petEventPublisher;

    @Mock
    private IAppointmentTaskEventPublisher taskEventPublisher;

    @InjectMocks
    private PetService petService;

    private Pet pet;
    private PetDTO petDTO;
    private AppointmentDTO appointmentDTO;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setOwnerId(1L);

        petDTO = new PetDTO(1L, "Buddy", "Dog", "Labrador", 5, "Healthy", "John Doe", 1L);

        appointmentDTO = new AppointmentDTO();
        appointmentDTO.setId(1L);
        appointmentDTO.setPetId(pet.getId());
        appointmentDTO.setVeterinarianName("Dr. Smith");
    }

    @Test
    void testGetAllPets() {
        when(petRepository.findAll()).thenReturn(List.of(pet));
        when(petMapper.toDTO(any(Pet.class))).thenReturn(petDTO);

        List<PetDTO> pets = petService.getAllPets();

        assertEquals(1, pets.size());
        verify(petRepository).findAll();
        verify(petMapper).toDTO(any(Pet.class));
    }

    @Test
    void testGetPetById_Success() {
        Long petId = 1L;
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(petMapper.toDTO(any(Pet.class))).thenReturn(petDTO);

        Optional<PetDTO> foundPet = petService.getPetById(petId);

        assertTrue(foundPet.isPresent());
        assertEquals(petId, foundPet.get().getId());
        verify(petRepository).findById(petId);
        verify(petMapper).toDTO(any(Pet.class));
    }

    @Test
    void testGetPetById_Failure_PetNotFound() {
        Long petId = 1L;
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        PetNotFoundException exception = assertThrows(PetNotFoundException.class, () -> {
            petService.getPetById(petId);
        });

        assertEquals("Pet not found with ID: " + petId, exception.getMessage());
        verify(petRepository).findById(petId);
        verify(petMapper, never()).toDTO(any());
    }

    @Test
    void testGetAllPetsByOwnerId() {
        Long ownerId = 1L;
        when(petRepository.findAllByOwnerId(ownerId)).thenReturn(List.of(pet));
        when(petMapper.toDTO(any(Pet.class))).thenReturn(petDTO);

        List<PetDTO> pets = petService.getAllPetsByOwnerId(ownerId);

        assertEquals(1, pets.size());
        verify(petRepository).findAllByOwnerId(ownerId);
        verify(petMapper).toDTO(any(Pet.class));
    }

    @Test
    void testCreatePet_Success() {
        when(ownerClient.ownerExists(pet.getOwnerId())).thenReturn(true);
        when(petMapper.toModel(any(PetDTO.class))).thenReturn(pet);
        when(petRepository.save(any(Pet.class))).thenReturn(pet);
        when(petMapper.toDTO(any(Pet.class))).thenReturn(petDTO);

        PetDTO savedPet = petService.createPet(petDTO);

        verify(petRepository).save(pet);
        verify(petEventPublisher).publishPetCreatedEvent(any());
        assertNotNull(savedPet);
        assertEquals(petDTO.getName(), savedPet.getName());
    }

    @Test
    void testCreatePet_Failure_OwnerNotFound() {
        when(ownerClient.ownerExists(pet.getOwnerId())).thenReturn(false);

        OwnerNotFoundException exception = assertThrows(OwnerNotFoundException.class, () -> petService.createPet(petDTO));

        assertEquals("Owner not found with ID: " + pet.getOwnerId(), exception.getMessage());
        verify(petRepository, never()).save(any());
        verify(petEventPublisher, never()).publishPetCreatedEvent(any());
    }

    @Test
    void testUpdatePet_Success() {
        Long petId = 1L;
        PetDTO updatedPetDTO = new PetDTO();
        updatedPetDTO.setId(petId);
        updatedPetDTO.setName("Max");

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);
        when(petMapper.toDTO(any(Pet.class))).thenReturn(updatedPetDTO);

        PetDTO result = petService.updatePet(petId, updatedPetDTO);

        verify(petRepository).save(pet);
        verify(petEventPublisher).publishPetUpdatedEvent(any());
        assertEquals("Max", result.getName());
    }

    @Test
    void testUpdatePet_Failure_PetNotFound() {
        Long petId = 1L;
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        PetNotFoundException exception = assertThrows(PetNotFoundException.class, () -> petService.updatePet(petId, petDTO));

        assertEquals("Pet not found with ID: " + petId, exception.getMessage());
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
    void testDeletePet_Failure_PetNotFound() {
        Long petId = 1L;
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        PetNotFoundException exception = assertThrows(PetNotFoundException.class, () -> petService.deletePet(petId));

        assertEquals("Pet not found with ID: " + petId, exception.getMessage());
        verify(petRepository, never()).deleteById(any());
        verify(petEventPublisher, never()).publishPetDeletedEvent(any());
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
