package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.common.dto.PetHealthSummary;
import com.wolfhack.vetoptim.petmanagement.model.Pet;
import com.wolfhack.vetoptim.petmanagement.service.PetHealthAnalyticsService;
import com.wolfhack.vetoptim.petmanagement.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

	private final PetService petService;

	private final PetHealthAnalyticsService petHealthAnalyticsService;

	@GetMapping
	public ResponseEntity<List<Pet>> getAllPets() {
		return ResponseEntity.ok(petService.getAllPets());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
		return ResponseEntity.of(petService.getPetById(id));
	}

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Pet>> getPetsByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(petService.getAllPetsByOwnerId(ownerId));
    }

	@GetMapping("/{petId}/health-summary")
	public ResponseEntity<PetHealthSummary> getPetHealthSummary(@PathVariable Long petId) {
		PetHealthSummary summary = petHealthAnalyticsService.getPetHealthSummary(petId);
		return ResponseEntity.ok(summary);
	}

	@PostMapping
	public ResponseEntity<Pet> createPet(@RequestBody Pet pet) {
		return ResponseEntity.ok(petService.createPet(pet));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Pet> updatePet(@PathVariable Long id, @RequestBody Pet petDetails) {
		return ResponseEntity.ok(petService.updatePet(id, petDetails));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePet(@PathVariable Long id) {
		petService.deletePet(id);
		return ResponseEntity.noContent().build();
	}

}