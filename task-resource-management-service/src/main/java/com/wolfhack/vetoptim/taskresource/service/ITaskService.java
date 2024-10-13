package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.dto.TaskDTO;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;

public interface ITaskService {

	List<TaskDTO> getAllTasks();

	@Cacheable("tasks")
	Optional<TaskDTO> getTaskById(Long id);

	TaskDTO createTask(TaskDTO taskDTO);

	TaskDTO updateTask(Long id, TaskDTO taskDTO);

	TaskDTO completeTask(Long id);

	TaskDTO failTask(Long id);

	TaskDTO escalateTask(Long id);

	void deleteTask(Long id);

}
