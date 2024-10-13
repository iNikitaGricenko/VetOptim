package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.common.dto.pet.MedicalRecordDTO;
import com.wolfhack.vetoptim.petmanagement.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/pets/{petId}/medical-records")
@RequiredArgsConstructor
@Tag(name = "Medical Record API", description = "API for managing medical records for pets")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @GetMapping
    @Operation(summary = "Get all medical records for a pet")
    public ResponseEntity<List<MedicalRecordDTO>> getMedicalHistory(@PathVariable("petId") Long petId) {
        List<MedicalRecordDTO> records = medicalRecordService.getMedicalHistoryForPet(petId);
        return ResponseEntity.ok(records);
    }

    @PostMapping
    @Operation(summary = "Create a new medical record for a pet")
    public ResponseEntity<MedicalRecordDTO> createMedicalRecord(@PathVariable("petId") Long petId, @Valid @RequestBody MedicalRecordDTO medicalRecordDTO) {
        MedicalRecordDTO createdRecord = medicalRecordService.createMedicalRecord(petId, medicalRecordDTO);
        return ResponseEntity.created(
            URI.create("/api/pets/" + petId + "/medical-records")
        ).body(createdRecord);
    }

    @PutMapping("/{recordId}")
    @Operation(summary = "Update an existing medical record")
    public ResponseEntity<MedicalRecordDTO> updateMedicalRecord(@PathVariable("petId") Long petId, @PathVariable("recordId") Long recordId, @Valid @RequestBody MedicalRecordDTO medicalRecordDTO) {
        MedicalRecordDTO updatedRecord = medicalRecordService.updateMedicalRecord(recordId, medicalRecordDTO);
        return ResponseEntity.ok(updatedRecord);
    }

    @DeleteMapping("/{recordId}")
    @Operation(summary = "Delete a medical record")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable("petId") Long petId, @PathVariable("recordId") Long recordId) {
        medicalRecordService.deleteMedicalRecord(recordId);
        return ResponseEntity.noContent().build();
    }
}
