package com.wolfhack.vetoptim.billing.repository;

import com.wolfhack.vetoptim.billing.model.ResourceCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceCostRepository extends JpaRepository<ResourceCost, Long> {
    Optional<ResourceCost> findByResourceId(Long resourceId);
}
