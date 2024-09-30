package com.wolfhack.vetoptim.owner.repository;

import com.wolfhack.vetoptim.owner.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
}