package com.wolfhack.vetoptim.taskresource.integration.controller;

import com.wolfhack.vetoptim.common.ResourceType;
import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
class ResourceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    void testCreateResource_Success() throws Exception {
        mockMvc.perform(post("/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"Surgical Kit\", \"quantity\": 5, \"type\": \"MEDICAL_SUPPLY\" }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Surgical Kit"))
            .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    void testGetAllResources_Success() throws Exception {
        Resource resource = new Resource();
        resource.setName("Surgical Kit");
        resource.setQuantity(5);
        resource.setType(ResourceType.MEDICAL_SUPPLY);
        resourceRepository.save(resource);

        mockMvc.perform(get("/resources")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Surgical Kit"));
    }
}
