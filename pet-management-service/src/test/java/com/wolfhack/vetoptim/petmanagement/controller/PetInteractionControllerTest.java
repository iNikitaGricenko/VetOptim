package com.wolfhack.vetoptim.petmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wolfhack.vetoptim.common.dto.pet.PetInteractionRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetInteractionResponseDTO;
import com.wolfhack.vetoptim.petmanagement.service.IPetInteractionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PetInteractionControllerTest {

    @Mock
    private IPetInteractionService petInteractionService;

    @InjectMocks
    private PetInteractionController petInteractionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(petInteractionController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testGetPetInteractions() throws Exception {
        List<PetInteractionResponseDTO> responseDTOs = Collections.emptyList();
        when(petInteractionService.getPetInteractions(1L)).thenReturn(responseDTOs);

        mockMvc.perform(get("/api/pets/1/interactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(petInteractionService).getPetInteractions(1L);
    }

    @Test
    void testLogInteraction() throws Exception {
        PetInteractionRequestDTO requestDTO = new PetInteractionRequestDTO("Playtime", "Playing with toy", LocalDateTime.now());
        PetInteractionResponseDTO responseDTO = new PetInteractionResponseDTO(1L, "Playtime", "Playing with toy", LocalDateTime.now(), 1L);

        when(petInteractionService.logInteraction(eq(1L), any(PetInteractionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/pets/1/interactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        verify(petInteractionService).logInteraction(eq(1L), any(PetInteractionRequestDTO.class));
    }

}
