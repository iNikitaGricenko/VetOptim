package com.wolfhack.vetoptim.taskresource.integration.service;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.model.TaskHistory;
import com.wolfhack.vetoptim.taskresource.repository.TaskHistoryRepository;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import com.wolfhack.vetoptim.taskresource.service.TaskHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
class TaskHistoryServiceIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskHistoryRepository taskHistoryRepository;

    @Autowired
    private TaskHistoryService taskHistoryService;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        taskHistoryRepository.deleteAll();
    }

    @Test
    void testLogTaskChange_Success() {
        Task task = new Task();
        task.setPetId(1L);
        task.setDescription("Checkup for pet");
        task.setStatus(TaskStatus.PENDING);
        task = taskRepository.save(task);

        taskHistoryService.logTaskChange(task, "Initial task creation");

        Optional<TaskHistory> loggedHistory = taskHistoryRepository.findAll().stream().findFirst();
        assertTrue(loggedHistory.isPresent());
        assertEquals("Initial task creation", loggedHistory.get().getDescription());
        assertEquals(TaskStatus.PENDING, loggedHistory.get().getStatus());
        assertNotNull(loggedHistory.get().getTimestamp());
    }
}
