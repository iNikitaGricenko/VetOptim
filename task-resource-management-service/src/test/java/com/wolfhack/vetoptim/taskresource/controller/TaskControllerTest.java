package com.wolfhack.vetoptim.taskresource.controller;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.dto.TaskDTO;
import com.wolfhack.vetoptim.taskresource.service.ITaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private ITaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void testGetAllTasks() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);

        when(taskService.getAllTasks()).thenReturn(List.of(taskDTO));

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void testGetTaskById() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);

        when(taskService.getTaskById(anyLong())).thenReturn(Optional.of(taskDTO));

        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void testCreateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);

        when(taskService.createTask(any(TaskDTO.class))).thenReturn(taskDTO);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"New Task\", \"petId\": 100, \"taskType\": \"CHECKUP\", \"deadline\": \"3024-10-13T17:05:34\", \"status\": \"PENDING\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L));

        verify(taskService, times(1)).createTask(any(TaskDTO.class));
    }

    @Test
    void testUpdateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);

        when(taskService.updateTask(anyLong(), any(TaskDTO.class))).thenReturn(taskDTO);

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"petId\": 0, \"taskType\": \"SURGERY\", \"deadline\": \"3024-10-13T17:05:34\", \"description\": \"test\", \"status\": \"COMPLETED\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));

        verify(taskService, times(1)).updateTask(anyLong(), any(TaskDTO.class));
    }

    @Test
    void testCompleteTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setStatus(TaskStatus.COMPLETED);

        when(taskService.completeTask(anyLong())).thenReturn(taskDTO);

        mockMvc.perform(put("/api/tasks/1/complete"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(taskService, times(1)).completeTask(1L);
    }

    @Test
    void testFailTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setStatus(TaskStatus.FAILED);

        when(taskService.failTask(anyLong())).thenReturn(taskDTO);

        mockMvc.perform(put("/api/tasks/1/fail"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("FAILED"));

        verify(taskService, times(1)).failTask(1L);
    }

    @Test
    void testEscalateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setStatus(TaskStatus.ESCALATED);

        when(taskService.escalateTask(anyLong())).thenReturn(taskDTO);

        mockMvc.perform(put("/api/tasks/1/escalate"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ESCALATED"));

        verify(taskService, times(1)).escalateTask(1L);
    }

    @Test
    void testDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }
}
