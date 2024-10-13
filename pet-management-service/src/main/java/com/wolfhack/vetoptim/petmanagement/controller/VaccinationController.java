package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.common.dto.pet.VaccinationRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.VaccinationResponseDTO;
import com.wolfhack.vetoptim.petmanagement.service.VaccinationService;
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
public class VaccinationController {

	private final VaccinationService vaccinationService;

	@GetMapping("/pet/{petId}")
	public ResponseEntity<List<VaccinationResponseDTO>> getVaccinationsForPet(@PathVariable("petId") Long petId) {
		List<VaccinationResponseDTO> vaccinations = vaccinationService.getVaccinationsForPet(petId);
		return ResponseEntity.ok(vaccinations);
	}

	@PostMapping("/pet/{petId}")
	public ResponseEntity<VaccinationResponseDTO> createVaccination(@PathVariable("petId") Long petId, @Valid @RequestBody VaccinationRequestDTO vaccination) {
		VaccinationResponseDTO createdVaccination = vaccinationService.createVaccination(petId, vaccination);
		return ResponseEntity.created(
			URI.create("/api/vaccinations/pet/" + petId)
		).body(createdVaccination);
	}

	@PutMapping("/{vaccinationId}")
	public ResponseEntity<VaccinationResponseDTO> updateVaccination(@PathVariable("vaccinationId") Long vaccinationId, @Valid @RequestBody VaccinationRequestDTO vaccinationDetails) {
		VaccinationResponseDTO updatedVaccination = vaccinationService.updateVaccination(vaccinationId, vaccinationDetails);
		return ResponseEntity.ok(updatedVaccination);
	}

	@DeleteMapping("/{vaccinationId}")
	public ResponseEntity<Void> deleteVaccination(@PathVariable("vaccinationId") Long vaccinationId) {
		vaccinationService.deleteVaccination(vaccinationId);
		return ResponseEntity.noContent().build();
	}

}