package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.petmanagement.model.Owner;
import com.wolfhack.vetoptim.petmanagement.repository.OwnerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

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
    void testGetAllOwners() {
        ownerService.getAllOwners();
        verify(ownerRepository).findAll();
    }

    @Test
    void testGetOwnerById_Success() {
        Long ownerId = 1L;
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(new Owner()));

        ownerService.getOwnerById(ownerId);

        verify(ownerRepository).findById(ownerId);
    }

    @Test
    void testCreateOwner_Success() {
        Owner owner = new Owner();
        owner.setName("John Doe");

        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);

        Owner savedOwner = ownerService.createOwner(owner);

        verify(ownerRepository).save(owner);
    }

    @Test
    void testUpdateOwner_Success() {
        Long ownerId = 1L;
        Owner ownerDetails = new Owner();
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(new Owner()));

        ownerService.updateOwner(ownerId, ownerDetails);

        verify(ownerRepository).save(any(Owner.class));
    }

    @Test
    void testDeleteOwner_Success() {
        Long ownerId = 1L;
        ownerService.deleteOwner(ownerId);

        verify(ownerRepository).deleteById(ownerId);
    }
}