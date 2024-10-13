package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.common.dto.pet.PetInteractionRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetInteractionResponseDTO;
import com.wolfhack.vetoptim.petmanagement.model.PetInteraction;
import com.wolfhack.vetoptim.petmanagement.service.PetInteractionService;
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
public class PetInteractionController {

	private final PetInteractionService petInteractionService;

	@GetMapping
	public ResponseEntity<List<PetInteractionResponseDTO>> getPetInteractions(@PathVariable("petId") Long petId) {
		return ResponseEntity.ok(petInteractionService.getPetInteractions(petId));
	}

	@PostMapping
	public ResponseEntity<PetInteractionResponseDTO> logInteraction(@PathVariable("petId") Long petId, @Valid @RequestBody PetInteractionRequestDTO interaction) {
		return ResponseEntity.created(
				URI.create("/api/pets/" + petId + "/interactions/")
			)
			.body(petInteractionService.logInteraction(petId, interaction));
	}

}