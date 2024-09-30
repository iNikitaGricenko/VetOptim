package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class ResourceForecastingServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceForecastingService resourceForecastingService;

	private AutoCloseable openedMocks;

	@BeforeEach
    void setUp() {
		openedMocks = MockitoAnnotations.openMocks(this);
    }

	@AfterEach
	void tearDown() throws Exception {
        openedMocks.close();
    }

    @Test
    void testForecastAndRestockResources() {
        Resource resource = new Resource();
        resource.setQuantity(2);

        when(resourceRepository.findAll()).thenReturn(List.of(resource));

        resourceForecastingService.forecastAndRestockResources();

        verify(resourceRepository).findAll();
        verify(resourceRepository).save(any(Resource.class));
    }

    @Test
    void testForecastAndRestockResources_SufficientStock() {
        Resource resource = new Resource();
        resource.setQuantity(10);

        when(resourceRepository.findAll()).thenReturn(List.of(resource));

        resourceForecastingService.forecastAndRestockResources();

        verify(resourceRepository).findAll();
        verify(resourceRepository, never()).save(any(Resource.class));
    }
}