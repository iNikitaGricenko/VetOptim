package com.wolfhack.vetoptim.taskresource.controller;

import com.wolfhack.vetoptim.common.dto.ResourceDTO;
import com.wolfhack.vetoptim.taskresource.service.IResourceService;
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
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Tag(name = "Resource API", description = "API for managing resources")
public class ResourceController {

    private final IResourceService resourceService;

    @GetMapping
    @Operation(summary = "Fetch all resources")
    public ResponseEntity<List<ResourceDTO>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    @PostMapping
    @Operation(summary = "Create a new resource")
    public ResponseEntity<ResourceDTO> createResource(@RequestBody @Valid ResourceDTO resource) {
        return ResponseEntity.created(
                URI.create("/api/resources/" + resource.getId())
            ).body(resourceService.createResource(resource));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing resource")
    public ResponseEntity<ResourceDTO> updateResource(@PathVariable("id") Long id, @RequestBody @Valid ResourceDTO resourceDTO) {
        return ResponseEntity.ok(resourceService.updateResource(id, resourceDTO));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a resource")
    public ResponseEntity<ResourceDTO> partialUpdateResource(@PathVariable("id") Long id, @RequestBody ResourceDTO resourceDTO) {
        return ResponseEntity.ok(resourceService.partialUpdateResource(id, resourceDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a resource")
    public ResponseEntity<Void> deleteResource(@PathVariable("id") Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}