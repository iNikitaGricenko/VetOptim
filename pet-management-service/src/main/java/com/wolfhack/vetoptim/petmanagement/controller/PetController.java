package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.common.dto.pet.PetDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetHealthSummary;
import com.wolfhack.vetoptim.petmanagement.service.PetHealthAnalyticsService;
import com.wolfhack.vetoptim.petmanagement.service.PetService;
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
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Tag(name = "Pet API", description = "API for managing pets and their health records")
public class PetController {

    private final PetService petService;
    private final PetHealthAnalyticsService petHealthAnalyticsService;

    @GetMapping
    @Operation(summary = "Get all pets")
    public ResponseEntity<List<PetDTO>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a pet by ID")
    public ResponseEntity<PetDTO> getPetById(@PathVariable("id") Long id) {
        return ResponseEntity.of(petService.getPetById(id));
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get all pets by owner ID")
    public ResponseEntity<List<PetDTO>> getPetsByOwnerId(@PathVariable("ownerId") Long ownerId) {
        return ResponseEntity.ok(petService.getAllPetsByOwnerId(ownerId));
    }

    @GetMapping("/{petId}/health-summary")
    @Operation(summary = "Get health summary for a pet")
    public ResponseEntity<PetHealthSummary> getPetHealthSummary(@PathVariable("petId") Long petId) {
        PetHealthSummary summary = petHealthAnalyticsService.getPetHealthSummary(petId);
        return ResponseEntity.ok(summary);
    }

    @PostMapping
    @Operation(summary = "Create a new pet")
    public ResponseEntity<PetDTO> createPet(@Valid @RequestBody PetDTO petDTO) {
        PetDTO created = petService.createPet(petDTO);
        return ResponseEntity.created(
            URI.create("/api/pets/" + created.getId())
        ).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing pet")
    public ResponseEntity<PetDTO> updatePet(@PathVariable("id") Long id, @Valid @RequestBody PetDTO petDetails) {
        return ResponseEntity.ok(petService.updatePet(id, petDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a pet by ID")
    public ResponseEntity<Void> deletePet(@PathVariable("id") Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}
