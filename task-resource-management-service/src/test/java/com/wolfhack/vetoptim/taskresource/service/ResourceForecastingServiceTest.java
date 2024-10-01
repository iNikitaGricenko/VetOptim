package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceForecastingServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceForecastingService resourceForecastingService;

    private Resource resource;

    @BeforeEach
    void setUp() {
        resource = new Resource();
        resource.setName("Vaccine");
        resource.setQuantity(3);
    }

    @Test
    void forecastAndRestockResources_TriggerRestock() {
        when(resourceRepository.findAll()).thenReturn(List.of(resource));

        resourceForecastingService.forecastAndRestockResources();

        verify(resourceRepository).save(resource);
    }

    @Test
    void forecastAndRestockResources_SufficientStock() {
        resource.setQuantity(10);
        when(resourceRepository.findAll()).thenReturn(List.of(resource));

        resourceForecastingService.forecastAndRestockResources();

        verify(resourceRepository, never()).save(resource);
    }
}
