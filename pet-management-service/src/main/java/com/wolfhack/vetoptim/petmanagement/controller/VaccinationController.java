package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.common.dto.pet.VaccinationRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.VaccinationResponseDTO;
import com.wolfhack.vetoptim.petmanagement.service.VaccinationService;
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
@RequestMapping("/api/vaccinations")
@RequiredArgsConstructor
@Tag(name = "Vaccination API", description = "API for managing vaccinations for pets")
public class VaccinationController {

	private final VaccinationService vaccinationService;

	@GetMapping("/pet/{petId}")
	@Operation(summary = "Get all vaccinations for a pet")
	public ResponseEntity<List<VaccinationResponseDTO>> getVaccinationsForPet(@PathVariable("petId") Long petId) {
		List<VaccinationResponseDTO> vaccinations = vaccinationService.getVaccinationsForPet(petId);
		return ResponseEntity.ok(vaccinations);
	}

	@PostMapping("/pet/{petId}")
	@Operation(summary = "Create a new vaccination for a pet")
	public ResponseEntity<VaccinationResponseDTO> createVaccination(@PathVariable("petId") Long petId, @Valid @RequestBody VaccinationRequestDTO vaccination) {
		VaccinationResponseDTO createdVaccination = vaccinationService.createVaccination(petId, vaccination);
		return ResponseEntity.created(
			URI.create("/api/vaccinations/pet/" + petId)
		).body(createdVaccination);
	}

	@PutMapping("/{vaccinationId}")
	@Operation(summary = "Update an existing vaccination")
	public ResponseEntity<VaccinationResponseDTO> updateVaccination(@PathVariable("vaccinationId") Long vaccinationId, @Valid @RequestBody VaccinationRequestDTO vaccinationDetails) {
		VaccinationResponseDTO updatedVaccination = vaccinationService.updateVaccination(vaccinationId, vaccinationDetails);
		return ResponseEntity.ok(updatedVaccination);
	}

	@DeleteMapping("/{vaccinationId}")
	@Operation(summary = "Delete a vaccination")
	public ResponseEntity<Void> deleteVaccination(@PathVariable("vaccinationId") Long vaccinationId) {
		vaccinationService.deleteVaccination(vaccinationId);
		return ResponseEntity.noContent().build();
	}

}