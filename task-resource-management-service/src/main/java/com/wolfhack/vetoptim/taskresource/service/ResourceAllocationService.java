package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.taskresource.client.BillingClient;
import com.wolfhack.vetoptim.taskresource.event.TaskEventPublisher;
import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.model.ResourceUsage;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceAllocationService {

    private final ResourceRepository resourceRepository;
    private final TaskRepository taskRepository;
    private final BillingClient billingClient;
    private final TaskEventPublisher eventPublisher;

    public void allocateResourcesForTask(Task task) {
        log.info("Allocating resources for Task ID: {} of type: {}", task.getId(), task.getTaskType());

        if (task.getTaskType() == TaskType.SURGERY) {
            Optional<Resource> surgicalKitOpt = resourceRepository.findByName("Surgical Kit");

            if (surgicalKitOpt.isPresent()) {
                Resource surgicalKit = surgicalKitOpt.get();

                if (surgicalKit.getQuantity() > 0) {
                    surgicalKit.setQuantity(surgicalKit.getQuantity() - 1);
                    resourceRepository.save(surgicalKit);

                    log.info("Surgical Kit allocated for Task ID: {}. Remaining quantity: {}", task.getId(), surgicalKit.getQuantity());

                    ResourceUsage resourceUsage = new ResourceUsage(surgicalKit.getId(), surgicalKit.getName(), 1);
                    task.addResourceUsage(resourceUsage);

                    if (surgicalKit.getQuantity() < 3) {
                        log.warn("Surgical Kit quantity is below threshold for Task ID: {}. Publishing depletion event.", task.getId());
                        eventPublisher.publishResourceDepletedEvent(new ResourceDepletedEvent(surgicalKit.getName(), surgicalKit.getQuantity()));
                    }

                    taskRepository.save(task);
                    log.info("Task ID: {} successfully updated after resource allocation.", task.getId());

                } else {
                    log.error("Surgical Kit is out of stock for Task ID: {}", task.getId());
                    throw new RuntimeException("Surgical Kit is out of stock.");
                }
            } else {
                log.error("Surgical Kit not found for Task ID: {}", task.getId());
                throw new RuntimeException("Surgical Kit not found.");
            }
        } else {
            log.info("No resources needed for Task ID: {} of type: {}", task.getId(), task.getTaskType());
        }
    }

}