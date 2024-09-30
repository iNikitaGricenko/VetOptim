package com.wolfhack.vetoptim.petmanagement.repository;

import com.wolfhack.vetoptim.petmanagement.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

	List<Pet> findAllByOwnerId(Long ownerId);

}