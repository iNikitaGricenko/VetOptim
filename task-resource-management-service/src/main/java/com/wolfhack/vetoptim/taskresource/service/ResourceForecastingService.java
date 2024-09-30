package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceForecastingService {

    private final ResourceRepository resourceRepository;

    public void forecastAndRestockResources() {
        log.info("Starting resource forecasting and restocking process.");

        List<Resource> resources = resourceRepository.findAll();

        for (Resource resource : resources) {
            if (resource.getQuantity() < 5) {
                log.warn("Resource {} is below minimum threshold. Current quantity: {}. Triggering restock.", resource.getName(), resource.getQuantity());
                triggerRestocking(resource);
            } else {
                log.info("Resource {} has sufficient quantity. Current quantity: {}", resource.getName(), resource.getQuantity());
            }
        }

        log.info("Resource forecasting and restocking process completed.");
    }

    private void triggerRestocking(Resource resource) {
        int restockAmount = 10;
        resource.setQuantity(resource.getQuantity() + restockAmount);
        resourceRepository.save(resource);

        log.info("Triggered restocking for Resource: {}. New quantity: {}", resource.getName(), resource.getQuantity());
    }
}