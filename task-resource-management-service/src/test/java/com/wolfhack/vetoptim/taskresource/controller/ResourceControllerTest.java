package com.wolfhack.vetoptim.taskresource.controller;

import com.wolfhack.vetoptim.common.dto.ResourceDTO;
import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.service.ResourceService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ResourceControllerTest {

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private ResourceController resourceController;

    private MockMvc mockMvc;

	private AutoCloseable openedMocks;

	@BeforeEach
    void setUp() {
		openedMocks = MockitoAnnotations.openMocks(this);
	    mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();
    }

	@AfterEach
	void tearDown() throws Exception {
        openedMocks.close();
    }

    @Test
    void testGetAllResources() throws Exception {
        when(resourceService.getAllResources()).thenReturn(List.of(new Resource()));

        mockMvc.perform(get("/resources"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(resourceService).getAllResources();
    }

    @Test
    void testCreateResource() throws Exception {
        Resource resource = new Resource();
        when(resourceService.createResource(any())).thenReturn(resource);

        mockMvc.perform(post("/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test Resource\"}"))
                .andExpect(status().isOk());

        verify(resourceService).createResource(any());
    }

    @Test
    void testUpdateResource() throws Exception {
        ResourceDTO resourceDTO = new ResourceDTO();
        Resource resource = new Resource();
        when(resourceService.updateResource(any(), any())).thenReturn(resource);

        mockMvc.perform(put("/resources/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Updated Resource\"}"))
                .andExpect(status().isOk());

        verify(resourceService).updateResource(any(), any());
    }

    @Test
    void testPartialUpdateResource() throws Exception {
        ResourceDTO resourceDTO = new ResourceDTO();
        Resource resource = new Resource();
        when(resourceService.partialUpdateResource(any(), any())).thenReturn(resource);

        mockMvc.perform(patch("/resources/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Partially Updated Resource\"}"))
                .andExpect(status().isOk());

        verify(resourceService).partialUpdateResource(any(), any());
    }

    @Test
    void testDeleteResource() throws Exception {
        mockMvc.perform(delete("/resources/{id}", 1))
                .andExpect(status().isOk());

        verify(resourceService).deleteResource(1L);
    }
}