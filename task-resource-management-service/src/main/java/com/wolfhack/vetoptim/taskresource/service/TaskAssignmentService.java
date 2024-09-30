package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.taskresource.model.Staff;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.StaffRepository;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskAssignmentService {

    private final StaffRepository staffRepository;
    private final TaskRepository taskRepository;

    public Optional<Staff> assignTaskToStaff(Task task) {
        log.info("Assigning task with ID: {} to staff", task.getId());
        List<Staff> availableStaff = staffRepository.findByRoleAndAvailableTrue(task.getTaskType().name());

        if (!availableStaff.isEmpty()) {
            Staff assignedStaff = availableStaff.getFirst();
            assignedStaff.setAvailable(false);
            staffRepository.save(assignedStaff);

            task.setStatus(TaskStatus.IN_PROGRESS);
            taskRepository.save(task);

            log.info("Task with ID: {} assigned to staff with ID: {}", task.getId(), assignedStaff.getId());

            return Optional.of(assignedStaff);
        } else {
            log.warn("No available staff found to assign task with ID: {}", task.getId());
            return Optional.empty();
        }
    }
}