package com.wolfhack.vetoptim.petmanagement.controller;

import com.wolfhack.vetoptim.common.dto.pet.PetDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetHealthSummary;
import com.wolfhack.vetoptim.petmanagement.service.PetHealthAnalyticsService;
import com.wolfhack.vetoptim.petmanagement.service.PetService;
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
public class PetController {

    private final PetService petService;
    private final PetHealthAnalyticsService petHealthAnalyticsService;

    @GetMapping
    public ResponseEntity<List<PetDTO>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getPetById(@PathVariable("id") Long id) {
        return ResponseEntity.of(petService.getPetById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PetDTO>> getPetsByOwnerId(@PathVariable("ownerId") Long ownerId) {
        return ResponseEntity.ok(petService.getAllPetsByOwnerId(ownerId));
    }

    @GetMapping("/{petId}/health-summary")
    public ResponseEntity<PetHealthSummary> getPetHealthSummary(@PathVariable("petId") Long petId) {
        PetHealthSummary summary = petHealthAnalyticsService.getPetHealthSummary(petId);
        return ResponseEntity.ok(summary);
    }

    @PostMapping
    public ResponseEntity<PetDTO> createPet(@Valid @RequestBody PetDTO petDTO) {
        PetDTO created = petService.createPet(petDTO);
        return ResponseEntity.created(
            URI.create("/api/pets/" + created.getId())
        ).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDTO> updatePet(@PathVariable("id") Long id, @Valid @RequestBody PetDTO petDetails) {
        return ResponseEntity.ok(petService.updatePet(id, petDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable("id") Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}
