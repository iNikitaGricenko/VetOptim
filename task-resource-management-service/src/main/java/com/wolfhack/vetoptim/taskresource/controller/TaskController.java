package com.wolfhack.vetoptim.taskresource.controller;

import com.wolfhack.vetoptim.common.dto.TaskDTO;
import com.wolfhack.vetoptim.taskresource.service.ITaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task API", description = "API for managing tasks")
public class TaskController {

    private final ITaskService ITaskService;

    @GetMapping
    @Operation(summary = "Fetch all tasks")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(ITaskService.getAllTasks());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable("id") Long id) {
        return ResponseEntity.of(ITaskService.getTaskById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO task) {
        return ResponseEntity.created(
                URI.create("/tasks/"+task.getId())
            )
            .body(ITaskService.createTask(task));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable("id") Long id, @Valid @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(ITaskService.updateTask(id, taskDTO));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Mark a task as completed")
    public ResponseEntity<TaskDTO> completeTask(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ITaskService.completeTask(id));
    }

    @PutMapping("/{id}/fail")
    @Operation(summary = "Mark a task as failed")
    public ResponseEntity<TaskDTO> failTask(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ITaskService.failTask(id));
    }

    @PutMapping("/{id}/escalate")
    @Operation(summary = "Escalate a task")
    public ResponseEntity<TaskDTO> escalateTask(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ITaskService.escalateTask(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        ITaskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
