package com.wolfhack.vetoptim.taskresource.service.integration;

import com.wolfhack.vetoptim.taskresource.client.BillingClient;
import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import com.wolfhack.vetoptim.taskresource.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
class ResourceServiceIntegrationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceService resourceService;

    @MockBean
    private BillingClient billingClient;

    @BeforeEach
    void setup() {
        resourceRepository.deleteAll();
    }

    @Test
    void testCreateResource_Success() throws Exception {
        Resource resource = new Resource();
        resource.setName("Surgical Kit");
        resource.setType(com.wolfhack.vetoptim.common.ResourceType.EQUIPMENT);
        resource.setQuantity(10);

        mockMvc.perform(post("/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"Surgical Kit\", \"type\": \"EQUIPMENT\", \"quantity\": 10 }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Surgical Kit"))
            .andExpect(jsonPath("$.quantity").value(10));

        Optional<Resource> savedResource = resourceRepository.findByName("Surgical Kit");
        assertTrue(savedResource.isPresent());
        assertEquals("Surgical Kit", savedResource.get().getName());
        assertEquals(10, savedResource.get().getQuantity());
    }

    @Test
    void testUpdateResource_Success() throws Exception {
        Resource resource = new Resource();
        resource.setName("Surgical Kit");
        resource.setType(com.wolfhack.vetoptim.common.ResourceType.EQUIPMENT);
        resource.setQuantity(10);
        resource = resourceRepository.save(resource);

        mockMvc.perform(put("/resources/{id}", resource.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"Surgical Kit\", \"type\": \"EQUIPMENT\", \"quantity\": 15 }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Surgical Kit"))
            .andExpect(jsonPath("$.quantity").value(15));

        Optional<Resource> updatedResource = resourceRepository.findById(resource.getId());
        assertTrue(updatedResource.isPresent());
        assertEquals(15, updatedResource.get().getQuantity());
    }

    @Test
    void testPartialUpdateResource_Success() throws Exception {
        Resource resource = new Resource();
        resource.setName("Surgical Kit");
        resource.setType(com.wolfhack.vetoptim.common.ResourceType.EQUIPMENT);
        resource.setQuantity(10);
        resource = resourceRepository.save(resource);

        mockMvc.perform(patch("/resources/{id}", resource.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"quantity\": 20 }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Surgical Kit"))
            .andExpect(jsonPath("$.quantity").value(20));

        Optional<Resource> updatedResource = resourceRepository.findById(resource.getId());
        assertTrue(updatedResource.isPresent());
        assertEquals(20, updatedResource.get().getQuantity());
    }

    @Test
    void testDeleteResource_Success() throws Exception {
        Resource resource = new Resource();
        resource.setName("Surgical Kit");
        resource.setType(com.wolfhack.vetoptim.common.ResourceType.EQUIPMENT);
        resource.setQuantity(10);
        resource = resourceRepository.save(resource);

        mockMvc.perform(delete("/resources/{id}", resource.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        Optional<Resource> deletedResource = resourceRepository.findById(resource.getId());
        assertFalse(deletedResource.isPresent());
    }

    @Test
    void testGetAllResources_Success() throws Exception {
        Resource resource1 = new Resource();
        resource1.setName("Surgical Kit");
        resource1.setType(com.wolfhack.vetoptim.common.ResourceType.EQUIPMENT);
        resource1.setQuantity(10);
        resourceRepository.save(resource1);

        Resource resource2 = new Resource();
        resource2.setName("Bandages");
        resource2.setType(com.wolfhack.vetoptim.common.ResourceType.MEDICAL_SUPPLY);
        resource2.setQuantity(20);
        resourceRepository.save(resource2);

        mockMvc.perform(get("/resources")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Surgical Kit"))
            .andExpect(jsonPath("$[1].name").value("Bandages"));
    }
}