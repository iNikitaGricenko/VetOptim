package com.wolfhack.vetoptim.taskresource.integration.service;

import com.wolfhack.vetoptim.common.ResourceType;
import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import com.wolfhack.vetoptim.taskresource.service.ResourceForecastingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
class ResourceForecastingServiceIntegrationTest {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceForecastingService resourceForecastingService;

    @BeforeEach
    void setup() {
        resourceRepository.deleteAll();
    }

    @Test
    void testForecastAndRestockResources() {
        Resource resource = new Resource();
        resource.setName("Surgical Kit");
        resource.setQuantity(2);
        resource.setType(ResourceType.MEDICAL_SUPPLY);
        resourceRepository.save(resource);

        resourceForecastingService.forecastAndRestockResources();

        Optional<Resource> updatedResource = resourceRepository.findById(resource.getId());
        assertTrue(updatedResource.isPresent());
        assertEquals(12, updatedResource.get().getQuantity());
    }
}
