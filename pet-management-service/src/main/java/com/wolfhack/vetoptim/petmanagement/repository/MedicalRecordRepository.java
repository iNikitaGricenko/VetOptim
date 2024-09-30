package com.wolfhack.vetoptim.petmanagement.repository;

import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

	List<MedicalRecord> findAllByPetId(Long petId);

}