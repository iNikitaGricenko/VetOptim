package com.wolfhack.vetoptim.taskresource.scheduler;

import com.wolfhack.vetoptim.taskresource.service.ResourceForecastingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceScheduler {

    private final ResourceForecastingService resourceForecastingService;

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleResourceForecasting() {
        log.info("Executing resource forecasting and restocking scheduler.");
        resourceForecastingService.forecastAndRestockResources();
        log.info("Resource forecasting and restocking process completed.");
    }
}