package com.wolfhack.vetoptim.billing.service;

import com.wolfhack.vetoptim.billing.model.TaskCost;
import com.wolfhack.vetoptim.billing.repository.TaskCostRepository;
import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.common.dto.billing.TaskCostDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskCostService {

    private final TaskCostRepository taskCostRepository;

    public TaskCost saveTaskCost(TaskCostDTO taskCostDTO) {
        TaskCost taskCost = new TaskCost();
        taskCost.setTaskType(taskCostDTO.getTaskType());
        taskCost.setCost(taskCostDTO.getCost());
        return taskCostRepository.save(taskCost);
    }

    public Optional<TaskCost> getTaskCost(TaskType taskType) {
        return taskCostRepository.findByTaskType(taskType);
    }
}