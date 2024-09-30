package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.petmanagement.client.AppointmentClient;
import com.wolfhack.vetoptim.petmanagement.event.PetEventPublisher;
import com.wolfhack.vetoptim.petmanagement.mapper.PetMapper;
import com.wolfhack.vetoptim.petmanagement.model.Pet;
import com.wolfhack.vetoptim.petmanagement.repository.PetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetMapper petMapper;

    @Mock
    private PetEventPublisher petEventPublisher;

    @Mock
    private AppointmentClient appointmentClient;

    @InjectMocks
    private PetService petService;

    private AutoCloseable openedMocks;

    @BeforeEach
    void setUp() {
        openedMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openedMocks.close();
    }

    @Test
    void testCreatePet_Success() {
        Pet pet = new Pet();
        pet.setName("Buddy");

        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        Pet savedPet = petService.createPet(pet);

        verify(petRepository).save(pet);
        verify(petEventPublisher).publishPetCreatedEvent(any());
    }

    @Test
    void testGetPetById_Success() {
        Long petId = 1L;
        when(petRepository.findById(petId)).thenReturn(Optional.of(new Pet()));

        petService.getPetById(petId);

        verify(petRepository).findById(petId);
    }

    @Test
    void testUpdatePet_Success() {
        Long petId = 1L;
        Pet petDetails = new Pet();
        when(petRepository.findById(petId)).thenReturn(Optional.of(new Pet()));

        petService.updatePet(petId, petDetails);

        verify(petRepository).save(any(Pet.class));
        verify(petEventPublisher).publishPetUpdatedEvent(any());
    }

    @Test
    void shouldUpdateOwnerInfoForPets() {
        Long ownerId = 1L;
        String newOwnerName = "John Updated";
        List<Pet> pets = List.of(new Pet(1L, "Buddy", "Dog", "Bulldog", 3, "", "John Doe", ownerId),
            new Pet(2L, "Max", "Dog", "Labrador", 5, "", "John Doe", ownerId));

        when(petRepository.findAllByOwnerId(ownerId)).thenReturn(pets);

        petService.updateOwnerInfoForPets(ownerId, newOwnerName);

        pets.forEach(pet -> {
            assertEquals(newOwnerName, pet.getOwnerName());
        });
        verify(petRepository, times(2)).save(any(Pet.class));
    }

    @Test
    void testDeletePet_Success() {
        Long petId = 1L;
        when(petRepository.findById(petId)).thenReturn(Optional.of(new Pet()));

        petService.deletePet(petId);

        verify(petRepository).deleteById(petId);
        verify(petEventPublisher).publishPetDeletedEvent(any());
    }
}