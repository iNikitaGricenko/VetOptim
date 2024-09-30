package com.wolfhack.vetoptim.owner.controller;

import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import com.wolfhack.vetoptim.owner.service.OwnerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.graalvm.nativeimage.RuntimeOptions.get;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerController.class)
public class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @Test
    public void shouldGetAllOwners() throws Exception {
        OwnerDTO ownerDTO = new OwnerDTO(1L, "John Doe", "john@example.com", true, false);

        Mockito.when(ownerService.getAllOwners()).thenReturn(Collections.singletonList(ownerDTO));

        mockMvc.perform(get("/owners"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name", is(ownerDTO.getName())))
            .andExpect(jsonPath("$[0].contactDetails", is(ownerDTO.getContactDetails())));
    }

    @Test
    public void shouldGetOwnerById() throws Exception {
        OwnerDTO ownerDTO = new OwnerDTO(1L, "John Doe", "john@example.com", true, false);

        Mockito.when(ownerService.getOwnerById(anyLong())).thenReturn(java.util.Optional.of(ownerDTO));

        mockMvc.perform(get("/owners/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(ownerDTO.getName())))
            .andExpect(jsonPath("$.contactDetails", is(ownerDTO.getContactDetails())));
    }

    @Test
    public void shouldReturnNotFoundForInvalidOwnerId() throws Exception {
        Mockito.when(ownerService.getOwnerById(anyLong())).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/owners/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateOwner() throws Exception {
        OwnerDTO ownerDTO = new OwnerDTO(1L, "John Doe", "john@example.com", true, false);

        Mockito.when(ownerService.createOwner(any(OwnerDTO.class))).thenReturn(ownerDTO);

        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"John Doe\", \"contactDetails\": \"john@example.com\", \"notifyByEmail\": true, \"notifyBySms\": false}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(ownerDTO.getName())))
            .andExpect(jsonPath("$.contactDetails", is(ownerDTO.getContactDetails())));
    }

    @Test
    public void shouldUpdateOwner() throws Exception {
        OwnerDTO ownerDTO = new OwnerDTO(1L, "John Doe", "john@example.com", true, false);

        Mockito.when(ownerService.updateOwner(anyLong(), any(OwnerDTO.class))).thenReturn(ownerDTO);

        mockMvc.perform(put("/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"John Doe\", \"contactDetails\": \"john@example.com\", \"notifyByEmail\": true, \"notifyBySms\": false}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(ownerDTO.getName())))
            .andExpect(jsonPath("$.contactDetails", is(ownerDTO.getContactDetails())));
    }

    @Test
    public void shouldDeleteOwner() throws Exception {
        mockMvc.perform(delete("/owners/1"))
            .andExpect(status().isNoContent());

        Mockito.verify(ownerService).deleteOwner(anyLong());
    }
}