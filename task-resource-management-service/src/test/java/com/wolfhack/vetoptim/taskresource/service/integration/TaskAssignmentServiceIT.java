package com.wolfhack.vetoptim.taskresource.service.integration;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.taskresource.model.Staff;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.StaffRepository;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import com.wolfhack.vetoptim.taskresource.service.TaskAssignmentService;
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
class TaskAssignmentServiceIT {

	@Autowired
	private TaskAssignmentService taskAssignmentService;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private StaffRepository staffRepository;

	@BeforeEach
	void setup() {
		taskRepository.deleteAll();
		staffRepository.deleteAll();

		Staff staff = new Staff();
		staff.setName("John Doe");
		staff.setRole(TaskType.SURGERY.name());
		staff.setAvailable(true);
		staffRepository.save(staff);
	}

	@Test
	void testAssignTaskToStaff_Success() {
		Task task = new Task();
		task.setPetId(1L);
		task.setTaskType(TaskType.SURGERY);
		task.setDescription("Surgery for pet");
		task.setStatus(TaskStatus.PENDING);
		task = taskRepository.save(task);

		Optional<Staff> assignedStaff = taskAssignmentService.assignTaskToStaff(task);
		assertTrue(assignedStaff.isPresent());
		assertEquals("John Doe", assignedStaff.get().getName());

		Task updatedTask = taskRepository.findById(task.getId()).orElseThrow();
		assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
		assertNotNull(updatedTask.getAssignedStaff());
	}
}
