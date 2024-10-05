package com.wolfhack.vetoptim.billing.repository;

import com.wolfhack.vetoptim.billing.model.TaskCost;
import com.wolfhack.vetoptim.common.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskCostRepository extends JpaRepository<TaskCost, Long> {
    Optional<TaskCost> findByTaskType(TaskType taskType);
}