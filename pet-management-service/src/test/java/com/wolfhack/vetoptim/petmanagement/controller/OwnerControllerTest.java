package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.petmanagement.model.Owner;
import com.wolfhack.vetoptim.petmanagement.service.OwnerService;
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

class OwnerControllerTest {

    @Mock
    private OwnerService ownerService;

    @InjectMocks
    private OwnerController ownerController;

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
    void testGetAllOwners_Success() {
        List<Owner> owners = List.of(new Owner());
        when(ownerService.getAllOwners()).thenReturn(owners);

        ResponseEntity<List<Owner>> response = ownerController.getAllOwners();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(owners, response.getBody());
    }

    @Test
    void testGetOwnerById_Success() {
        Long ownerId = 1L;
        Owner owner = new Owner();
        when(ownerService.getOwnerById(ownerId)).thenReturn(Optional.of(owner));

        ResponseEntity<Owner> response = ownerController.getOwnerById(ownerId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(owner, response.getBody());
    }

    @Test
    void testCreateOwner_Success() {
        Owner owner = new Owner();
        when(ownerService.createOwner(owner)).thenReturn(owner);

        ResponseEntity<Owner> response = ownerController.createOwner(owner);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(owner, response.getBody());
    }

    @Test
    void testUpdateOwner_Success() {
        Long ownerId = 1L;
        Owner ownerDetails = new Owner();
        when(ownerService.updateOwner(ownerId, ownerDetails)).thenReturn(ownerDetails);

        ResponseEntity<Owner> response = ownerController.updateOwner(ownerId, ownerDetails);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(ownerDetails, response.getBody());
    }

    @Test
    void testDeleteOwner_Success() {
        Long ownerId = 1L;

        ResponseEntity<Void> response = ownerController.deleteOwner(ownerId);

        assertEquals(204, response.getStatusCode().value());
        verify(ownerService).deleteOwner(ownerId);
    }
}