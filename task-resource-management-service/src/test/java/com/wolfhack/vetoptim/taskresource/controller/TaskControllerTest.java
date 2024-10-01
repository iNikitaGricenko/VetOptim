package com.wolfhack.vetoptim.taskresource.controller;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.dto.TaskDTO;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.service.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void testGetAllTasks() throws Exception {
        Task task = new Task();
        task.setId(1L);

        when(taskService.getAllTasks()).thenReturn(List.of(task));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void testGetTaskById() throws Exception {
        Task task = new Task();
        task.setId(1L);

        when(taskService.getTaskById(anyLong())).thenReturn(Optional.of(task));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void testCreateTask() throws Exception {
        Task task = new Task();
        task.setId(1L);

        when(taskService.createTask(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"New Task\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void testUpdateTask() throws Exception {
        Task task = new Task();
        task.setId(1L);

        when(taskService.updateTask(anyLong(), any(TaskDTO.class))).thenReturn(task);

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(taskService, times(1)).updateTask(anyLong(), any(TaskDTO.class));
    }

    @Test
    void testCompleteTask() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.COMPLETED);

        when(taskService.completeTask(anyLong())).thenReturn(task);

        mockMvc.perform(put("/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(taskService, times(1)).completeTask(1L);
    }

    @Test
    void testFailTask() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.FAILED);

        when(taskService.failTask(anyLong())).thenReturn(task);

        mockMvc.perform(put("/tasks/1/fail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"));

        verify(taskService, times(1)).failTask(1L);
    }

    @Test
    void testEscalateTask() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.ESCALATED);

        when(taskService.escalateTask(anyLong())).thenReturn(task);

        mockMvc.perform(put("/tasks/1/escalate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ESCALATED"));

        verify(taskService, times(1)).escalateTask(1L);
    }

    @Test
    void testDeleteTask() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }
}