package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.taskresource.event.TaskEventPublisher;
import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

    @InjectMocks
    private ResourceAllocationService resourceAllocationService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTaskType(TaskType.SURGERY);
    }

    @Test
    void allocateResourcesForTask_Success() {
        Resource surgicalKit = new Resource();
        surgicalKit.setName("Surgical Kit");
        surgicalKit.setQuantity(5);

        when(resourceRepository.findByName("Surgical Kit")).thenReturn(Optional.of(surgicalKit));

        resourceAllocationService.allocateResourcesForTask(task);

        verify(resourceRepository).save(surgicalKit);
        verify(taskRepository).save(task);
    }

    @Test
    void allocateResourcesForTask_ResourceDepleted() {
        Resource surgicalKit = new Resource();
        surgicalKit.setName("Surgical Kit");
        surgicalKit.setQuantity(2);

        when(resourceRepository.findByName("Surgical Kit")).thenReturn(Optional.of(surgicalKit));

        resourceAllocationService.allocateResourcesForTask(task);

        verify(eventPublisher).publishResourceDepletedEvent(any(ResourceDepletedEvent.class));
    }

    @Test
    void allocateResourcesForTask_ResourceOutOfStock() {
        Resource surgicalKit = new Resource();
        surgicalKit.setName("Surgical Kit");
        surgicalKit.setQuantity(0);

        when(resourceRepository.findByName("Surgical Kit")).thenReturn(Optional.of(surgicalKit));

        assertThrows(RuntimeException.class, () -> resourceAllocationService.allocateResourcesForTask(task));
    }

    @Test
    void allocateResourcesForTask_ResourceNotFound() {
        when(resourceRepository.findByName("Surgical Kit")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> resourceAllocationService.allocateResourcesForTask(task));
    }

    @Test
    void allocateResourcesForNonSurgeryTask_NoAllocationRequired() {
        task.setTaskType(TaskType.CHECKUP);

        resourceAllocationService.allocateResourcesForTask(task);

        verify(resourceRepository, never()).findByName(anyString());
        verify(taskRepository, never()).save(any());
    }
}