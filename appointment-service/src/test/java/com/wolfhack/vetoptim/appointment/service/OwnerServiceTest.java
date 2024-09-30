package com.wolfhack.vetoptim.appointment.service;

import com.wolfhack.vetoptim.appointment.model.Owner;
import com.wolfhack.vetoptim.appointment.repository.OwnerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

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
    void testGetAllOwners() {
        Owner owner = new Owner();
        when(ownerRepository.findAll()).thenReturn(List.of(owner));

        List<Owner> owners = ownerService.getAllOwners();

        assertNotNull(owners);
        assertEquals(1, owners.size());
    }

    @Test
    void testGetOwnerById_success() {
        Long ownerId = 1L;
        Owner owner = new Owner();
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        Optional<Owner> result = ownerService.getOwnerById(ownerId);

        assertTrue(result.isPresent());
        assertEquals(owner, result.get());
    }

    @Test
    void testGetOwnerById_notFound() {
        Long ownerId = 1L;
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.empty());

        Optional<Owner> result = ownerService.getOwnerById(ownerId);

        assertFalse(result.isPresent());
    }

    @Test
    void testCreateOwner() {
        Owner owner = new Owner();
        when(ownerRepository.save(owner)).thenReturn(owner);

        Owner result = ownerService.createOwner(owner);

        assertNotNull(result);
        verify(ownerRepository).save(owner);
    }

    @Test
    void testUpdateOwner_success() {
        Long ownerId = 1L;
        Owner existingOwner = new Owner();
        Owner updatedDetails = new Owner();
        updatedDetails.setName("New Name");

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(existingOwner));
        when(ownerRepository.save(existingOwner)).thenReturn(existingOwner);

        Owner result = ownerService.updateOwner(ownerId, updatedDetails);

        assertNotNull(result);
        assertEquals("New Name", existingOwner.getName());
        verify(ownerRepository).save(existingOwner);
    }

    @Test
    void testUpdateOwner_notFound() {
        Long ownerId = 1L;
        Owner updatedDetails = new Owner();

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ownerService.updateOwner(ownerId, updatedDetails));
    }

    @Test
    void testDeleteOwner() {
        Long ownerId = 1L;

        ownerService.deleteOwner(ownerId);

        verify(ownerRepository).deleteById(ownerId);
    }
}