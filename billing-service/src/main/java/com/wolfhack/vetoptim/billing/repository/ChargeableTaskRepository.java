package com.wolfhack.vetoptim.billing.repository;

import com.wolfhack.vetoptim.billing.model.ChargeableTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChargeableTaskRepository extends JpaRepository<ChargeableTask, Long> {
	List<ChargeableTask> findByInvoiceOwnerId(Long ownerId);

	Optional<ChargeableTask> findByTaskId(Long taskId);
}
