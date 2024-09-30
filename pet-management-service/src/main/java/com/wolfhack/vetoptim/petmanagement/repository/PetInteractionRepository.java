package com.wolfhack.vetoptim.petmanagement.repository;

import com.wolfhack.vetoptim.petmanagement.model.PetInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetInteractionRepository extends JpaRepository<PetInteraction, Long> {

	List<PetInteraction> findAllByPetId(Long petId);

}