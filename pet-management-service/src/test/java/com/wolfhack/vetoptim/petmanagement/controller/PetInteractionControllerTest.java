package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.petmanagement.model.PetInteraction;
import com.wolfhack.vetoptim.petmanagement.service.PetInteractionService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PetInteractionControllerTest {

    @Mock
    private PetInteractionService petInteractionService;

    @InjectMocks
    private PetInteractionController petInteractionController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(petInteractionController).build();
    }

    @Test
    void testGetPetInteractions() throws Exception {
        when(petInteractionService.getPetInteractions(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pets/1/interactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(petInteractionService).getPetInteractions(1L);
    }

    @Test
    void testLogInteraction() throws Exception {
        PetInteraction interaction = new PetInteraction();
        when(petInteractionService.logInteraction(any(PetInteraction.class))).thenReturn(interaction);

        mockMvc.perform(post("/pets/1/interactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"interactionType\": \"Playtime\"}"))
                .andExpect(status().isOk());

        verify(petInteractionService).logInteraction(any(PetInteraction.class));
    }

}
