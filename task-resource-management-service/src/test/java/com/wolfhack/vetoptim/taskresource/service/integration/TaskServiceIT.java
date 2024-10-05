package com.wolfhack.vetoptim.taskresource.service.integration;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.common.dto.PetDTO;
import com.wolfhack.vetoptim.common.dto.billing.TaskBillingRequest;
import com.wolfhack.vetoptim.common.event.task.TaskCompletedEvent;
import com.wolfhack.vetoptim.common.event.task.TaskCreatedEvent;
import com.wolfhack.vetoptim.taskresource.client.BillingClient;
import com.wolfhack.vetoptim.taskresource.client.PetClient;
import com.wolfhack.vetoptim.taskresource.event.TaskEventPublisher;
import com.wolfhack.vetoptim.taskresource.model.Staff;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import com.wolfhack.vetoptim.taskresource.repository.StaffRepository;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import com.wolfhack.vetoptim.taskresource.service.ResourceAllocationService;
import com.wolfhack.vetoptim.taskresource.service.TaskAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
class TaskServiceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private StaffRepository staffRepository;

    @MockBean
    private PetClient petClient;

    @MockBean
    private BillingClient billingClient;

    @MockBean
    private TaskEventPublisher taskEventPublisher;

    @MockBean
    private TaskAssignmentService taskAssignmentService;

    @MockBean
    private ResourceAllocationService resourceAllocationService;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        resourceRepository.deleteAll();
        staffRepository.deleteAll();

        Staff staff = new Staff();
        staff.setName("John Doe");
        staff.setRole("Vet");
        staff.setAvailable(true);
        staffRepository.save(staff);
    }

    @Test
    void testCreateTask_Success() throws Exception {
        Task task = new Task();
        task.setPetId(1L);
        task.setTaskType(TaskType.SURGERY);
        task.setDescription("Routine surgery for pet");
        task.setStatus(TaskStatus.PENDING);

        when(petClient.getPetById(1L)).thenReturn(new PetDTO(1L, "Buddy", "Dog", "Labrador", 5));

        doNothing().when(resourceAllocationService).allocateResourcesForTask(any(Task.class));

        doNothing().when(taskEventPublisher).publishTaskCreatedEvent(any());

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"petId\": 1, \"taskType\": \"SURGERY\", \"description\": \"Routine surgery for pet\" }"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.petId").value(1))
            .andExpect(jsonPath("$.taskType").value("SURGERY"))
            .andExpect(jsonPath("$.description").value("Routine surgery for pet"));

        verify(petClient, times(1)).getPetById(1L);
        verify(resourceAllocationService, times(1)).allocateResourcesForTask(any(Task.class));
        verify(taskEventPublisher, times(1)).publishTaskCreatedEvent(any(TaskCreatedEvent.class));

        Optional<Task> savedTask = taskRepository.findAll().stream().findFirst();
        assertTrue(savedTask.isPresent());
        assertNotNull(savedTask.get().getAssignedStaff());
        assertEquals("John Doe", savedTask.get().getAssignedStaff().getName());
    }

    @Test
    void testUpdateTaskStatus_CompleteTask() throws Exception {
        Task task = new Task();
        task.setPetId(1L);
        task.setTaskType(TaskType.SURGERY);
        task.setDescription("Routine surgery for pet");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task = taskRepository.save(task);

        doNothing().when(billingClient).sendTaskBillingRequest(any());

        doNothing().when(taskEventPublisher).publishTaskCompletedEvent(any());

        mockMvc.perform(put("/tasks/{id}/complete", task.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(billingClient, times(1)).sendTaskBillingRequest(any(TaskBillingRequest.class));
        verify(taskEventPublisher, times(1)).publishTaskCompletedEvent(any(TaskCompletedEvent.class));

        Optional<Task> completedTask = taskRepository.findById(task.getId());
        assertTrue(completedTask.isPresent());
        assertEquals(TaskStatus.COMPLETED, completedTask.get().getStatus());
    }

    @Test
    void testGetTaskById_Success() throws Exception {
        Task task = new Task();
        task.setPetId(1L);
        task.setTaskType(TaskType.SURGERY);
        task.setDescription("Routine surgery for pet");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task = taskRepository.save(task);

        mockMvc.perform(get("/tasks/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(task.getId()))
            .andExpect(jsonPath("$.taskType").value("SURGERY"));
    }

    @Test
    void testDeleteTask_Success() throws Exception {
        Task task = new Task();
        task.setPetId(1L);
        task.setTaskType(TaskType.SURGERY);
        task.setDescription("Routine surgery for pet");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task = taskRepository.save(task);

        mockMvc.perform(delete("/tasks/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        Optional<Task> deletedTask = taskRepository.findById(task.getId());
        assertFalse(deletedTask.isPresent());
    }
}
