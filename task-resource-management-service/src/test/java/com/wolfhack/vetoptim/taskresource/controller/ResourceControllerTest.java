package com.wolfhack.vetoptim.taskresource.controller;

import com.wolfhack.vetoptim.common.dto.ResourceDTO;
import com.wolfhack.vetoptim.taskresource.service.IResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {

    @Mock
    private IResourceService resourceService;

    @InjectMocks
    private ResourceController resourceController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();
    }

    @Test
    void testGetAllResources() throws Exception {
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setId(1L);
        resourceDTO.setName("Test Resource");

        when(resourceService.getAllResources()).thenReturn(List.of(resourceDTO));

        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Resource"));

        verify(resourceService, times(1)).getAllResources();
    }

    @Test
    void testCreateResource() throws Exception {
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setId(1L);
        resourceDTO.setName("Test Resource");

        when(resourceService.createResource(any(ResourceDTO.class))).thenReturn(resourceDTO);

        mockMvc.perform(post("/api/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test Resource\", \"type\": \"MEDICAL_SUPPLY\", \"quantity\": 10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Resource"));

        verify(resourceService, times(1)).createResource(any(ResourceDTO.class));
    }

    @Test
    void testUpdateResource() throws Exception {
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setId(1L);
        resourceDTO.setName("Updated Resource");

        when(resourceService.updateResource(anyLong(), any(ResourceDTO.class))).thenReturn(resourceDTO);

        mockMvc.perform(put("/api/resources/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Updated Resource\", \"type\": \"EQUIPMENT\", \"quantity\": 5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Resource"));

        verify(resourceService, times(1)).updateResource(anyLong(), any(ResourceDTO.class));
    }

    @Test
    void testPartialUpdateResource() throws Exception {
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setId(1L);
        resourceDTO.setName("Partially Updated Resource");

        when(resourceService.partialUpdateResource(anyLong(), any(ResourceDTO.class))).thenReturn(resourceDTO);

        mockMvc.perform(patch("/api/resources/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Partially Updated Resource\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Partially Updated Resource"));

        verify(resourceService, times(1)).partialUpdateResource(anyLong(), any(ResourceDTO.class));
    }

    @Test
    void testDeleteResource() throws Exception {
        mockMvc.perform(delete("/api/resources/1"))
                .andExpect(status().isNoContent());

        verify(resourceService, times(1)).deleteResource(1L);
    }
}