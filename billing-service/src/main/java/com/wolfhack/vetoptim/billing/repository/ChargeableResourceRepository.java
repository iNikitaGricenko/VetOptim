package com.wolfhack.vetoptim.billing.repository;

import com.wolfhack.vetoptim.billing.model.ChargeableResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChargeableResourceRepository extends JpaRepository<ChargeableResource, Long> {
	List<ChargeableResource> findByInvoiceOwnerId(Long ownerId);

	Optional<ChargeableResource> findByResourceId(Long resourceId);

}
