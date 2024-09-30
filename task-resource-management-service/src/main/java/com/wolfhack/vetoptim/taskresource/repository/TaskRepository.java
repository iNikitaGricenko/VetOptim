package com.wolfhack.vetoptim.taskresource.repository;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.taskresource.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findAllByStatusAndDeadlineBetween(TaskStatus taskStatus, LocalDateTime from, LocalDateTime to);

	List<Task> findAllByPetId(Long petId);

}