package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.taskresource.model.Staff;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.StaffRepository;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskAssignmentServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskAssignmentService taskAssignmentService;

	private AutoCloseable openedMocks;

	@BeforeEach
    void setUp() {
		openedMocks = MockitoAnnotations.openMocks(this);
    }

	@AfterEach
	void tearDown() throws Exception {
        openedMocks.close();
    }

    @Test
    void testAssignTaskToStaff_Success() {
        Task task = new Task();
        task.setTaskType(TaskType.CHECKUP);

        Staff staff = new Staff();
        staff.setAvailable(true);

        when(staffRepository.findByRoleAndAvailableTrue(anyString())).thenReturn(List.of(staff));
        when(taskRepository.save(any())).thenReturn(task);

        Optional<Staff> assignedStaff = taskAssignmentService.assignTaskToStaff(task);

        assertTrue(assignedStaff.isPresent());
        assertFalse(assignedStaff.get().isAvailable());
        verify(staffRepository).save(any(Staff.class));
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testAssignTaskToStaff_NoAvailableStaff() {
        Task task = new Task();
        task.setTaskType(TaskType.CHECKUP);

        when(staffRepository.findByRoleAndAvailableTrue(anyString())).thenReturn(List.of());

        Optional<Staff> assignedStaff = taskAssignmentService.assignTaskToStaff(task);

        assertFalse(assignedStaff.isPresent());
        verify(staffRepository, never()).save(any(Staff.class));
        verify(taskRepository, never()).save(any(Task.class));
    }
}