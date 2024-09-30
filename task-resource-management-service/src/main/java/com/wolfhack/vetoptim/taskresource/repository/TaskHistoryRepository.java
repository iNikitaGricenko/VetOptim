package com.wolfhack.vetoptim.taskresource.repository;

import com.wolfhack.vetoptim.taskresource.model.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
}
