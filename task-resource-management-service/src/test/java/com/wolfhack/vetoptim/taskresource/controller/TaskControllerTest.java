package com.wolfhack.vetoptim.taskresource.controller;

import com.wolfhack.vetoptim.common.dto.TaskDTO;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.service.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;

	private AutoCloseable openedMocks;

	@BeforeEach
    void setUp() {
		openedMocks = MockitoAnnotations.openMocks(this);
	    mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

	@AfterEach
	void tearDown() throws Exception {
        openedMocks.close();
    }

    @Test
    void testGetAllTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(new Task()));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(taskService).getAllTasks();
    }

    @Test
    void testGetTaskById() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(new Task()));

        mockMvc.perform(get("/tasks/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(taskService).getTaskById(1L);
    }

    @Test
    void testCreateTask() throws Exception {
        Task task = new Task();
        when(taskService.createTask(any())).thenReturn(task);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"New Task\"}"))
                .andExpect(status().isOk());

        verify(taskService).createTask(any());
    }

    @Test
    void testUpdateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        Task task = new Task();
        when(taskService.updateTask(any(), any())).thenReturn(task);

        mockMvc.perform(put("/tasks/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"Updated Task\"}"))
                .andExpect(status().isOk());

        verify(taskService).updateTask(any(), any());
    }

    @Test
    void testDeleteTask() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", 1))
                .andExpect(status().isOk());

        verify(taskService).deleteTask(1L);
    }
}