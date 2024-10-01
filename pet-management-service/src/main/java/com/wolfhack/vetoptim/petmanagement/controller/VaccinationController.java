package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.petmanagement.model.Vaccination;
import com.wolfhack.vetoptim.petmanagement.service.VaccinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vaccinations")
@RequiredArgsConstructor
public class VaccinationController {

	private final VaccinationService vaccinationService;

	@GetMapping("/pet/{petId}")
	public ResponseEntity<List<Vaccination>> getVaccinationsForPet(@PathVariable("petId") Long petId) {
		List<Vaccination> vaccinations = vaccinationService.getVaccinationsForPet(petId);
		return ResponseEntity.ok(vaccinations);
	}

	@PostMapping("/pet/{petId}")
	public ResponseEntity<Vaccination> createVaccination(@PathVariable("petId") Long petId, @RequestBody Vaccination vaccination) {
		Vaccination createdVaccination = vaccinationService.createVaccination(petId, vaccination);
		return ResponseEntity.ok(createdVaccination);
	}

	@PutMapping("/{vaccinationId}")
	public ResponseEntity<Vaccination> updateVaccination(@PathVariable("vaccinationId") Long vaccinationId, @RequestBody Vaccination vaccinationDetails) {
		Vaccination updatedVaccination = vaccinationService.updateVaccination(vaccinationId, vaccinationDetails);
		return ResponseEntity.ok(updatedVaccination);
	}

	@DeleteMapping("/{vaccinationId}")
	public ResponseEntity<Void> deleteVaccination(@PathVariable("vaccinationId") Long vaccinationId) {
		vaccinationService.deleteVaccination(vaccinationId);
		return ResponseEntity.noContent().build();
	}

}