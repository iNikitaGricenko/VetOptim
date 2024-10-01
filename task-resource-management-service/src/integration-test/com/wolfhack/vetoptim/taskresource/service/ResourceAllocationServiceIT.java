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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
class ResourceAllocationServiceIT {

    @Autowired
    private ResourceAllocationService resourceAllocationService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @MockBean
    private BillingClient billingClient;

    @MockBean
    private TaskEventPublisher taskEventPublisher;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        resourceRepository.deleteAll();
    }

    @Test
    void testAllocateResourcesForTask_Success() {
        Task task = new Task();
        task.setPetId(1L);
        task.setTaskType(TaskType.SURGERY);
        task.setDescription("Surgery task");
        task = taskRepository.save(task);

        Resource surgicalKit = new Resource();
        surgicalKit.setName("Surgical Kit");
        surgicalKit.setType(com.wolfhack.vetoptim.common.ResourceType.EQUIPMENT);
        surgicalKit.setQuantity(5);
        resourceRepository.save(surgicalKit);

        resourceAllocationService.allocateResourcesForTask(task);

        Optional<Task> updatedTask = taskRepository.findById(task.getId());
        assertTrue(updatedTask.isPresent());
        assertEquals(1, updatedTask.get().getResourcesUsed().size());

        ResourceUsage resourceUsage = updatedTask.get().getResourcesUsed().get(0);
        assertEquals("Surgical Kit", resourceUsage.getResourceName());
        assertEquals(1, resourceUsage.getQuantityUsed());

        Optional<Resource> updatedResource = resourceRepository.findById(surgicalKit.getId());
        assertTrue(updatedResource.isPresent());
        assertEquals(4, updatedResource.get().getQuantity());

        verify(taskEventPublisher, times(0)).publishResourceDepletedEvent(any(ResourceDepletedEvent.class));
    }

    @Test
    void testAllocateResourcesForTask_ResourceDepletionEvent() {
        Task task = new Task();
        task.setPetId(1L);
        task.setTaskType(TaskType.SURGERY);
        task.setDescription("Surgery task");
        task = taskRepository.save(task);

        Resource surgicalKit = new Resource();
        surgicalKit.setName("Surgical Kit");
        surgicalKit.setType(com.wolfhack.vetoptim.common.ResourceType.EQUIPMENT);
        surgicalKit.setQuantity(2);
        resourceRepository.save(surgicalKit);

        resourceAllocationService.allocateResourcesForTask(task);

        Optional<Task> updatedTask = taskRepository.findById(task.getId());
        assertTrue(updatedTask.isPresent());
        assertEquals(1, updatedTask.get().getResourcesUsed().size());

        ResourceUsage resourceUsage = updatedTask.get().getResourcesUsed().getFirst();
        assertEquals("Surgical Kit", resourceUsage.getResourceName());
        assertEquals(1, resourceUsage.getQuantityUsed());

        Optional<Resource> updatedResource = resourceRepository.findById(surgicalKit.getId());
        assertTrue(updatedResource.isPresent());
        assertEquals(1, updatedResource.get().getQuantity());

        verify(taskEventPublisher, times(1)).publishResourceDepletedEvent(any(ResourceDepletedEvent.class));
    }

    @Test
    void testAllocateResourcesForTask_OutOfStock() {
        Task task = new Task();
        task.setPetId(1L);
        task.setTaskType(TaskType.SURGERY);
        task.setDescription("Surgery task");
        task = taskRepository.save(task);

        Resource surgicalKit = new Resource();
        surgicalKit.setName("Surgical Kit");
        surgicalKit.setType(com.wolfhack.vetoptim.common.ResourceType.EQUIPMENT);
        surgicalKit.setQuantity(0);
        resourceRepository.save(surgicalKit);

        Task finalTask = task;
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            resourceAllocationService.allocateResourcesForTask(finalTask)
        );

        assertEquals("Surgical Kit is out of stock.", exception.getMessage());

        Optional<Task> updatedTask = taskRepository.findById(task.getId());
        assertTrue(updatedTask.isPresent());
        assertEquals(0, updatedTask.get().getResourcesUsed().size());

        verify(taskEventPublisher, times(0)).publishResourceDepletedEvent(any(ResourceDepletedEvent.class));
    }

    @Test
    void testAllocateResourcesForTask_NoResourcesNeeded() {
        Task task = new Task();
        task.setPetId(1L);
        task.setTaskType(TaskType.CHECKUP);
        task.setDescription("Checkup task");
        task = taskRepository.save(task);

        resourceAllocationService.allocateResourcesForTask(task);

        Optional<Task> updatedTask = taskRepository.findById(task.getId());
        assertTrue(updatedTask.isPresent());
        assertEquals(0, updatedTask.get().getResourcesUsed().size());

        verify(taskEventPublisher, times(0)).publishResourceDepletedEvent(any(ResourceDepletedEvent.class));
    }
}
