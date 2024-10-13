package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.common.dto.pet.PetDTO;
import com.wolfhack.vetoptim.petmanagement.service.PetHealthAnalyticsService;
import com.wolfhack.vetoptim.petmanagement.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class PetControllerTest {

    @Mock
    private PetService petService;

    @Mock
    private PetHealthAnalyticsService petHealthAnalyticsService;

    @InjectMocks
    private PetController petController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(petController).build();
    }

    @Test
    void testGetAllPets() throws Exception {
        when(petService.getAllPets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/pets"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(petService).getAllPets();
    }

    @Test
    void testGetPetById() throws Exception {
        when(petService.getPetById(1L)).thenReturn(Optional.of(new PetDTO()));

        mockMvc.perform(get("/api/pets/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(petService).getPetById(1L);
    }

    @Test
    void testGetPetsByOwnerId() throws Exception {
        when(petService.getAllPetsByOwnerId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/pets/owner/1"))
            .andExpect(status().isOk());

        verify(petService).getAllPetsByOwnerId(1L);
    }

    @Test
    void testCreatePet() throws Exception {
        PetDTO pet = new PetDTO();
        when(petService.createPet(any(PetDTO.class))).thenReturn(pet);

        mockMvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Buddy\"}"))
            .andExpect(status().isCreated());

        verify(petService).createPet(any(PetDTO.class));
    }

    @Test
    void testUpdatePet() throws Exception {
        PetDTO pet = new PetDTO();
        when(petService.updatePet(anyLong(), any(PetDTO.class))).thenReturn(pet);

        mockMvc.perform(put("/api/pets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Buddy\"}"))
            .andExpect(status().isOk());

        verify(petService).updatePet(anyLong(), any(PetDTO.class));
    }

    @Test
    void testDeletePet() throws Exception {
        mockMvc.perform(delete("/api/pets/1"))
            .andExpect(status().isNoContent());

        verify(petService).deletePet(1L);
    }
}