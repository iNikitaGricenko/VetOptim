package com.wolfhack.vetoptim.billing.controller;

import com.wolfhack.vetoptim.billing.model.TaskCost;
import com.wolfhack.vetoptim.billing.repository.TaskCostRepository;
import com.wolfhack.vetoptim.billing.service.TaskCostService;
import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.common.dto.billing.TaskCostDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@Validated
@RestController
@RequestMapping("/api/costs/task")
@RequiredArgsConstructor
public class TaskCostController {

    private final TaskCostService taskCostService;

    @PostMapping
    public ResponseEntity<TaskCost> setTaskCost(@Valid @RequestBody TaskCostDTO taskCostDTO) {
        TaskCost savedTaskCost = taskCostService.saveTaskCost(taskCostDTO);
        return ResponseEntity.created(
            URI.create("/api/costs/task/" + savedTaskCost.getId())
        ).body(savedTaskCost);
    }

    @GetMapping("/{taskType}")
    public ResponseEntity<TaskCost> getTaskCost(@PathVariable TaskType taskType) {
        return taskCostService.getTaskCost(taskType)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task cost not found"));
    }
}