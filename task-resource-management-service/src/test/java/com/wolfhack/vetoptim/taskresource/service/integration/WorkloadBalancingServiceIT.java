package com.wolfhack.vetoptim.taskresource.service.integration;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.taskresource.model.Staff;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.StaffRepository;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import com.wolfhack.vetoptim.taskresource.service.WorkloadBalancingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
class WorkloadBalancingServiceIT {

    @Autowired
    private WorkloadBalancingService workloadBalancingService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StaffRepository staffRepository;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        staffRepository.deleteAll();

        Staff staff1 = new Staff();
        staff1.setName("John Doe");
        staff1.setRole("SURGERY");
        staff1.setAvailable(true);
        staffRepository.save(staff1);

        Staff staff2 = new Staff();
        staff2.setName("Jane Smith");
        staff2.setRole("SURGERY");
        staff2.setAvailable(true);
        staffRepository.save(staff2);
    }

    @Test
    void testBalanceWorkloadAndAssignTask_Success() {
        Task task = new Task();
        task.setPetId(1L);
        task.setTaskType(TaskType.SURGERY);
        task.setDescription("Surgery for pet");
        task.setStatus(TaskStatus.PENDING);
        task = taskRepository.save(task);

        workloadBalancingService.balanceWorkloadAndAssignTask(task);

        Task updatedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertNotNull(updatedTask.getAssignedStaff());
        assertFalse(updatedTask.getAssignedStaff().isAvailable());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }

}
