package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.petmanagement.model.PetInteraction;
import com.wolfhack.vetoptim.petmanagement.service.PetInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets/{petId}/interactions")
@RequiredArgsConstructor
public class PetInteractionController {

	private final PetInteractionService petInteractionService;

	@GetMapping
	public ResponseEntity<List<PetInteraction>> getPetInteractions(@PathVariable Long petId) {
		return ResponseEntity.ok(petInteractionService.getPetInteractions(petId));
	}

	@PostMapping
	public ResponseEntity<PetInteraction> logInteraction(@PathVariable Long petId, @RequestBody PetInteraction interaction) {
		return ResponseEntity.ok(petInteractionService.logInteraction(interaction));
	}

}