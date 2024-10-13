package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.pet.MedicalRecordDTO;
import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;

import java.util.List;

public interface IMedicalRecordService {

	List<MedicalRecordDTO> getMedicalHistoryForPet(Long petId);

	MedicalRecordDTO createMedicalRecord(Long petId, MedicalRecordDTO medicalRecordDTO);

	MedicalRecord createMedicalRecordFromAppointment(Long petId, String diagnosis, String treatment);

	MedicalRecordDTO updateMedicalRecord(Long recordId, MedicalRecordDTO medicalRecordDTO);

	void deleteMedicalRecord(Long recordId);

}
