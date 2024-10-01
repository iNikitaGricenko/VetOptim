package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import com.wolfhack.vetoptim.petmanagement.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets/{petId}/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

	private final MedicalRecordService medicalRecordService;

	@GetMapping
	public ResponseEntity<List<MedicalRecord>> getMedicalHistory(@PathVariable("petId") Long petId) {
		List<MedicalRecord> records = medicalRecordService.getMedicalHistoryForPet(petId);
		return ResponseEntity.ok(records);
	}

	@PostMapping
	public ResponseEntity<MedicalRecord> createMedicalRecord(@PathVariable("petId") Long petId, @RequestBody MedicalRecord medicalRecord) {
		MedicalRecord createdRecord = medicalRecordService.createMedicalRecord(petId, medicalRecord);
		return ResponseEntity.ok(createdRecord);
	}

	@PutMapping("/{recordId}")
	public ResponseEntity<MedicalRecord> updateMedicalRecord(@PathVariable("petId") String petId, @PathVariable("recordId") Long recordId, @RequestBody MedicalRecord medicalRecord) {
		MedicalRecord updatedRecord = medicalRecordService.updateMedicalRecord(recordId, medicalRecord);
		return ResponseEntity.ok(updatedRecord);
	}

	@DeleteMapping("/{recordId}")
	public ResponseEntity<Void> deleteMedicalRecord(@PathVariable("petId") String petId, @PathVariable("recordId") Long recordId) {
		medicalRecordService.deleteMedicalRecord(recordId);
		return ResponseEntity.noContent().build();
	}

}