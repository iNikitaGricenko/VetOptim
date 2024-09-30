package com.wolfhack.vetoptim.appointment.controller;

import com.wolfhack.vetoptim.appointment.model.Owner;
import com.wolfhack.vetoptim.appointment.service.OwnerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OwnerControllerTest {

    @Mock
    private OwnerService ownerService;

    @InjectMocks
    private OwnerController ownerController;

    private MockMvc mockMvc;

	private AutoCloseable openedMocks;

	@BeforeEach
    void setUp() {
		openedMocks = MockitoAnnotations.openMocks(this);
	    mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
    }

	@AfterEach
	void tearDown() throws Exception {
        openedMocks.close();
    }

    @Test
    void testGetAllOwners() throws Exception {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setName("John Doe");

        when(ownerService.getAllOwners()).thenReturn(List.of(owner));

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        verify(ownerService, times(1)).getAllOwners();
    }

    @Test
    void testGetOwnerById_success() throws Exception {
        Long ownerId = 1L;
        Owner owner = new Owner();
        owner.setId(ownerId);
        owner.setName("John Doe");

        when(ownerService.getOwnerById(ownerId)).thenReturn(Optional.of(owner));

        mockMvc.perform(get("/owners/{id}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ownerId))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(ownerService, times(1)).getOwnerById(ownerId);
    }

    @Test
    void testGetOwnerById_notFound() throws Exception {
        Long ownerId = 1L;

        when(ownerService.getOwnerById(ownerId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/owners/{id}", ownerId))
                .andExpect(status().isNotFound());

        verify(ownerService, times(1)).getOwnerById(ownerId);
    }

    @Test
    void testCreateOwner() throws Exception {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setName("John Doe");

        when(ownerService.createOwner(any())).thenReturn(owner);

        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"John Doe\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(ownerService, times(1)).createOwner(any());
    }

    @Test
    void testUpdateOwner_success() throws Exception {
        Long ownerId = 1L;
        Owner updatedOwner = new Owner();
        updatedOwner.setId(ownerId);
        updatedOwner.setName("Updated Name");

        when(ownerService.updateOwner(eq(ownerId), any())).thenReturn(updatedOwner);

        mockMvc.perform(put("/owners/{id}", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Updated Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ownerId))
                .andExpect(jsonPath("$.name").value("Updated Name"));

        verify(ownerService, times(1)).updateOwner(eq(ownerId), any());
    }

    @Test
    void testUpdateOwner_notFound() throws Exception {
        Long ownerId = 1L;

        when(ownerService.updateOwner(eq(ownerId), any())).thenThrow(new RuntimeException("Owner not found"));

        mockMvc.perform(put("/owners/{id}", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Updated Name\"}"))
                .andExpect(status().isNotFound());

        verify(ownerService, times(1)).updateOwner(eq(ownerId), any());
    }

    @Test
    void testDeleteOwner_success() throws Exception {
        Long ownerId = 1L;

        mockMvc.perform(delete("/owners/{id}", ownerId))
                .andExpect(status().isNoContent());

        verify(ownerService, times(1)).deleteOwner(ownerId);
    }
}