package com.wolfhack.vetoptim.petmanagement.repository;

import com.wolfhack.vetoptim.petmanagement.model.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {

	List<Vaccination> findAllByPetId(Long petId);

}