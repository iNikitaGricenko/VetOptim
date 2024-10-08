package com.wolfhack.vetoptim.owner.controller;

import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import com.wolfhack.vetoptim.owner.service.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    @Mock
    private OwnerService ownerService;

    @InjectMocks
    private OwnerController ownerController;

    @Test
    void getAllOwners_Success() {
        OwnerDTO ownerDTO = new OwnerDTO();
        List<OwnerDTO> owners = List.of(ownerDTO);
        when(ownerService.getAllOwners()).thenReturn(owners);

        ResponseEntity<List<OwnerDTO>> response = ownerController.getAllOwners();

        assertEquals(OK, response.getStatusCode());
        assertEquals(owners, response.getBody());
        verify(ownerService).getAllOwners();
    }

    @Test
    void getOwnerById_Success() {
        Long ownerId = 1L;
        OwnerDTO ownerDTO = new OwnerDTO();
        when(ownerService.getOwnerById(ownerId)).thenReturn(Optional.of(ownerDTO));

        ResponseEntity<OwnerDTO> response = ownerController.getOwnerById(ownerId);

        assertEquals(OK, response.getStatusCode());
        assertEquals(ownerDTO, response.getBody());
        verify(ownerService).getOwnerById(ownerId);
    }

    @Test
    void getOwnerById_NotFound() {
        Long ownerId = 1L;
        when(ownerService.getOwnerById(ownerId)).thenReturn(Optional.empty());

        ResponseEntity<OwnerDTO> response = ownerController.getOwnerById(ownerId);

        assertEquals(ResponseEntity.of(Optional.empty()), response);
        verify(ownerService).getOwnerById(ownerId);
    }

    @Test
    void createOwner_Success() {
        OwnerDTO ownerDTO = new OwnerDTO();
        when(ownerService.createOwner(ownerDTO)).thenReturn(ownerDTO);

        ResponseEntity<OwnerDTO> response = ownerController.createOwner(ownerDTO);

        assertEquals(OK, response.getStatusCode());
        assertEquals(ownerDTO, response.getBody());
        verify(ownerService).createOwner(ownerDTO);
    }

    @Test
    void updateOwner_Success() {
        Long ownerId = 1L;
        OwnerDTO ownerDTO = new OwnerDTO();
        when(ownerService.updateOwner(ownerId, ownerDTO)).thenReturn(ownerDTO);

        ResponseEntity<OwnerDTO> response = ownerController.updateOwner(ownerId, ownerDTO);

        assertEquals(OK, response.getStatusCode());
        assertEquals(ownerDTO, response.getBody());
        verify(ownerService).updateOwner(ownerId, ownerDTO);
    }

    @Test
    void deleteOwner_Success() {
        Long ownerId = 1L;
        doNothing().when(ownerService).deleteOwner(ownerId);

        ResponseEntity<Void> response = ownerController.deleteOwner(ownerId);

        assertEquals(NO_CONTENT, response.getStatusCode());
        verify(ownerService).deleteOwner(ownerId);
    }
}
