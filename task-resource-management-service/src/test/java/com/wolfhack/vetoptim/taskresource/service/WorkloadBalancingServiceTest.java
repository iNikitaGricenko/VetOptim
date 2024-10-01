package com.wolfhack.vetoptim.taskresource.service;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadBalancingServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private WorkloadBalancingService workloadBalancingService;

    @Test
    void balanceWorkloadAndAssignTask_Success() {
        Task task = new Task();
        task.setId(1L);

        Staff staff = new Staff();
        staff.setId(1L);
        staff.setAvailable(true);

        List<Staff> availableStaff = List.of(staff);

        when(staffRepository.findAvailableStaffSortedByWorkload()).thenReturn(availableStaff);
        when(taskRepository.save(task)).thenReturn(task);

        workloadBalancingService.balanceWorkloadAndAssignTask(task);

        verify(taskRepository).save(task);
        verify(staffRepository).save(staff);
        assertEquals(staff, task.getAssignedStaff());
        assertFalse(staff.isAvailable());
    }

    @Test
    void balanceWorkloadAndAssignTask_NoStaffAvailable() {
        Task task = new Task();
        task.setId(1L);

        when(staffRepository.findAvailableStaffSortedByWorkload()).thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            workloadBalancingService.balanceWorkloadAndAssignTask(task);
        });

        assertEquals("No available staff to assign the task.", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
        verify(staffRepository, never()).save(any(Staff.class));
    }
}
