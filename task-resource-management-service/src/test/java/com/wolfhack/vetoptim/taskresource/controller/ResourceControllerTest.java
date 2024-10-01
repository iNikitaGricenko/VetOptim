package com.wolfhack.vetoptim.taskresource.controller;

import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.service.ResourceService;
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
    private ResourceService resourceService;

    @InjectMocks
    private ResourceController resourceController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();
    }

    @Test
    void testGetAllResources() throws Exception {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Test Resource");

        when(resourceService.getAllResources()).thenReturn(List.of(resource));

        mockMvc.perform(get("/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Resource"));

        verify(resourceService, times(1)).getAllResources();
    }

    @Test
    void testCreateResource() throws Exception {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Test Resource");

        when(resourceService.createResource(any(Resource.class))).thenReturn(resource);

        mockMvc.perform(post("/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test Resource\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Resource"));

        verify(resourceService, times(1)).createResource(any(Resource.class));
    }

    @Test
    void testUpdateResource() throws Exception {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Updated Resource");

        when(resourceService.updateResource(anyLong(), any())).thenReturn(resource);

        mockMvc.perform(put("/resources/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Updated Resource\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Resource"));

        verify(resourceService, times(1)).updateResource(anyLong(), any());
    }

    @Test
    void testPartialUpdateResource() throws Exception {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Partially Updated Resource");

        when(resourceService.partialUpdateResource(anyLong(), any())).thenReturn(resource);

        mockMvc.perform(patch("/resources/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Partially Updated Resource\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Partially Updated Resource"));

        verify(resourceService, times(1)).partialUpdateResource(anyLong(), any());
    }

    @Test
    void testDeleteResource() throws Exception {
        mockMvc.perform(delete("/resources/1"))
                .andExpect(status().isNoContent());

        verify(resourceService, times(1)).deleteResource(1L);
    }
}