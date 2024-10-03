package com.wolfhack.vetoptim.billing.controller;

import com.wolfhack.vetoptim.billing.model.ResourceCost;
import com.wolfhack.vetoptim.billing.repository.ResourceCostRepository;
import com.wolfhack.vetoptim.billing.service.ResourceCostService;
import com.wolfhack.vetoptim.common.dto.billing.ResourceCostDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@Validated
@RestController
@RequestMapping("/api/costs/resource")
@RequiredArgsConstructor
public class ResourceCostController {

    private final ResourceCostService resourceCostService;

    @PostMapping
    public ResponseEntity<ResourceCost> setResourceCost(@Valid @RequestBody ResourceCostDTO resourceCostDTO) {
        ResourceCost savedResourceCost = resourceCostService.saveResourceCost(resourceCostDTO);
        return ResponseEntity.created(
            URI.create("/api/costs/resource/" + savedResourceCost.getId())
        ).body(savedResourceCost);
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceCost> getResourceCost(@PathVariable Long resourceId) {
        return resourceCostService.getResourceCost(resourceId)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource cost not found"));
    }
}