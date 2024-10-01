package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.taskresource.model.Staff;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.StaffRepository;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskAssignmentServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskAssignmentService taskAssignmentService;

    @Test
    void assignTaskToStaff_Success() {
        Task task = new Task();
        task.setId(1L);
        task.setTaskType(TaskType.SURGERY);

        Staff staff = new Staff();
        staff.setId(1L);
        staff.setAvailable(true);

        List<Staff> availableStaff = List.of(staff);

        when(staffRepository.findByRoleAndAvailableTrue(task.getTaskType().name())).thenReturn(availableStaff);
        when(taskRepository.save(task)).thenReturn(task);

        Optional<Staff> assignedStaff = taskAssignmentService.assignTaskToStaff(task);

        assertTrue(assignedStaff.isPresent());
        assertEquals(staff, assignedStaff.get());
        verify(staffRepository).save(staff);
        verify(taskRepository).save(task);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertFalse(staff.isAvailable());
    }

    @Test
    void assignTaskToStaff_NoStaffAvailable() {
        Task task = new Task();
        task.setId(1L);
        task.setTaskType(TaskType.SURGERY);

        when(staffRepository.findByRoleAndAvailableTrue(task.getTaskType().name())).thenReturn(List.of());

        Optional<Staff> assignedStaff = taskAssignmentService.assignTaskToStaff(task);

        assertFalse(assignedStaff.isPresent());
        verify(taskRepository, never()).save(any(Task.class));
        verify(staffRepository, never()).save(any(Staff.class));
    }
}
