package com.wolfhack.vetoptim.taskresource.controller;

import com.wolfhack.vetoptim.common.dto.TaskDTO;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public Optional<Task> getTaskById(@PathVariable("id") Long id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable("id") Long id, @RequestBody TaskDTO taskDTO) {
        return taskService.updateTask(id, taskDTO);
    }

    @PutMapping("/{id}/complete")
    public Task completeTask(@PathVariable("id") Long id) {
	    return taskService.completeTask(id);
    }

    @PutMapping("/{id}/fail")
    public Task failTask(@PathVariable("id") Long id) {
	    return taskService.failTask(id);
    }

    @PutMapping("/{id}/escalate")
    public Task escalateTask(@PathVariable("id") Long id) {
	    return taskService.escalateTask(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable("id") Long id) {
        taskService.deleteTask(id);
    }
}
