package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.common.dto.billing.ResourceBillingRequest;
import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.taskresource.client.BillingClient;
import com.wolfhack.vetoptim.taskresource.event.TaskEventPublisher;
import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceAllocationServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskEventPublisher eventPublisher;

    @Mock
    private BillingClient billingClient;

    @InjectMocks
    private ResourceAllocationService resourceAllocationService;

    @Test
    void testAllocateResourcesForTask_Success() {
        Task task = new Task();
        task.setTaskType(TaskType.SURGERY);

        Resource resource = new Resource();
        resource.setName("Surgical Kit");
        resource.setQuantity(5);

        when(resourceRepository.findByName("Surgical Kit")).thenReturn(Optional.of(resource));

        resourceAllocationService.allocateResourcesForTask(task);

        assertThat(task.getResourcesUsed()).hasSize(1);
        verify(resourceRepository).save(resource);
            verify(billingClient).sendResourceBillingRequest(any(ResourceBillingRequest.class));
        verify(taskRepository).save(task);
        verify(eventPublisher, never()).publishResourceDepletedEvent(any());
    }

    @Test
    void testAllocateResourcesForTask_ResourceDepleted() {
        Task task = new Task();
        task.setTaskType(TaskType.SURGERY);

        Resource resource = new Resource();
        resource.setName("Surgical Kit");
        resource.setQuantity(2);

        when(resourceRepository.findByName("Surgical Kit")).thenReturn(Optional.of(resource));

        resourceAllocationService.allocateResourcesForTask(task);

        verify(resourceRepository).save(resource);
        verify(taskRepository).save(task);
        verify(eventPublisher).publishResourceDepletedEvent(any(ResourceDepletedEvent.class));
    }

    @Test
    void testAllocateResourcesForTask_OutOfStock() {
        Task task = new Task();
        task.setTaskType(TaskType.SURGERY);

        Resource resource = new Resource();
        resource.setName("Surgical Kit");
        resource.setQuantity(0);

        when(resourceRepository.findByName("Surgical Kit")).thenReturn(Optional.of(resource));

        assertThrows(RuntimeException.class, () -> resourceAllocationService.allocateResourcesForTask(task));

        verify(resourceRepository, never()).save(resource);
        verify(taskRepository, never()).save(task);
        verify(eventPublisher, never()).publishResourceDepletedEvent(any());
    }

    @Test
    void testAllocateResourcesForTask_ResourceNotFound() {
        Task task = new Task();
        task.setTaskType(TaskType.SURGERY);

        when(resourceRepository.findByName("Surgical Kit")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> resourceAllocationService.allocateResourcesForTask(task));

        verify(resourceRepository, never()).save(any(Resource.class));
        verify(taskRepository, never()).save(any(Task.class));
        verify(eventPublisher, never()).publishResourceDepletedEvent(any());
    }
}