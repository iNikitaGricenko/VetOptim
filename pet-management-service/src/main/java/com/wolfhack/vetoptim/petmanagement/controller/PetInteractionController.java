package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.common.dto.pet.PetInteractionRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetInteractionResponseDTO;
import com.wolfhack.vetoptim.petmanagement.service.IPetInteractionService;
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
@RequestMapping("/api/pets/{petId}/interactions")
@RequiredArgsConstructor
@Tag(name = "Pet Interaction API", description = "API for managing interactions with pets")
public class PetInteractionController {

	private final IPetInteractionService petInteractionService;

	@GetMapping
	@Operation(summary = "Get all interactions for a pet")
	public ResponseEntity<List<PetInteractionResponseDTO>> getPetInteractions(@PathVariable("petId") Long petId) {
		return ResponseEntity.ok(petInteractionService.getPetInteractions(petId));
	}

	@PostMapping
	@Operation(summary = "Log a new interaction for a pet")
	public ResponseEntity<PetInteractionResponseDTO> logInteraction(@PathVariable("petId") Long petId, @Valid @RequestBody PetInteractionRequestDTO interaction) {
		return ResponseEntity.created(
				URI.create("/api/pets/" + petId + "/interactions/")
			)
			.body(petInteractionService.logInteraction(petId, interaction));
	}

}