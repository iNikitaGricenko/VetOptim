package com.wolfhack.vetoptim.taskresource.scheduler;

import com.wolfhack.vetoptim.taskresource.service.ResourceForecastingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class ResourceSchedulerTest {

    @Mock
    private ResourceForecastingService resourceForecastingService;

    @InjectMocks
    private ResourceScheduler resourceScheduler;

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
    void testScheduleResourceForecasting() {
        resourceScheduler.scheduleResourceForecasting();
        verify(resourceForecastingService).forecastAndRestockResources();
    }
}