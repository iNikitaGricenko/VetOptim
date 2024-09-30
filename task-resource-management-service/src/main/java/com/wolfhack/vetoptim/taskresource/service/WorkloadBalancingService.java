package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.taskresource.model.Staff;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.StaffRepository;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadBalancingService {

    private final StaffRepository staffRepository;
    private final TaskRepository taskRepository;

    public void balanceWorkloadAndAssignTask(Task task) {
        log.info("Balancing workload and assigning task with ID: {}", task.getId());
        List<Staff> availableStaff = staffRepository.findAvailableStaffSortedByWorkload();

        if (!availableStaff.isEmpty()) {
            Staff leastBusyStaff = availableStaff.getFirst();
            task.setAssignedStaff(leastBusyStaff);
            taskRepository.save(task);

            leastBusyStaff.setAvailable(false);
            staffRepository.save(leastBusyStaff);

            log.info("Task ID: {} assigned to staff ID: {}", task.getId(), leastBusyStaff.getId());
        } else {
            log.error("No available staff to assign task with ID: {}", task.getId());
            throw new RuntimeException("No available staff to assign the task.");
        }
    }
}