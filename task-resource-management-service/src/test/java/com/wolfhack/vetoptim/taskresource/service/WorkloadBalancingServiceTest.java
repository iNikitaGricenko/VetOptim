package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.taskresource.model.Staff;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.StaffRepository;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class WorkloadBalancingServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private WorkloadBalancingService workloadBalancingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBalanceWorkloadAndAssignTask_Success() {
        Task task = new Task();
        Staff staff = new Staff();

        when(staffRepository.findAvailableStaffSortedByWorkload()).thenReturn(List.of(staff));

        workloadBalancingService.balanceWorkloadAndAssignTask(task);

        verify(staffRepository).findAvailableStaffSortedByWorkload();
        verify(taskRepository).save(task);
        verify(staffRepository).save(staff);
        assertEquals(staff, task.getAssignedStaff());
    }

    @Test
    void testBalanceWorkloadAndAssignTask_NoAvailableStaff() {
        Task task = new Task();

        when(staffRepository.findAvailableStaffSortedByWorkload()).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> workloadBalancingService.balanceWorkloadAndAssignTask(task));

        verify(staffRepository).findAvailableStaffSortedByWorkload();
        verify(taskRepository, never()).save(any());
    }
}