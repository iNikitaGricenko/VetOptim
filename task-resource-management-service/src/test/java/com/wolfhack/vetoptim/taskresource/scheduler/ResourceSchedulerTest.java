package com.wolfhack.vetoptim.taskresource.scheduler;

import com.wolfhack.vetoptim.taskresource.service.ResourceForecastingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResourceSchedulerTest {

    @Mock
    private ResourceForecastingService resourceForecastingService;

    @InjectMocks
    private ResourceScheduler resourceScheduler;

    @Test
    void testScheduleResourceForecasting() {
        resourceScheduler.scheduleResourceForecasting();

        verify(resourceForecastingService).forecastAndRestockResources();
    }
}
