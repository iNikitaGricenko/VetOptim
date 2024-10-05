package com.wolfhack.vetoptim.owner.controller;

import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import com.wolfhack.vetoptim.owner.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    public ResponseEntity<List<OwnerDTO>> getAllOwners() {
        return ResponseEntity.ok(ownerService.getAllOwners());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerDTO> getOwnerById(@PathVariable("id") Long id) {
        return ResponseEntity.of(ownerService.getOwnerById(id));
    }

    @PostMapping
    public ResponseEntity<OwnerDTO> createOwner(@Valid @RequestBody OwnerDTO ownerDTO) {
        OwnerDTO owner = ownerService.createOwner(ownerDTO);
        return ResponseEntity.created(
		        URI.create("/api/owners/" + owner.getId())
            )
            .body(owner);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OwnerDTO> updateOwner(@PathVariable("id") Long id, @Valid @RequestBody OwnerDTO ownerDTO) {
        return ResponseEntity.ok(ownerService.updateOwner(id, ownerDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwner(@PathVariable("id") Long id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.noContent().build();
    }
}