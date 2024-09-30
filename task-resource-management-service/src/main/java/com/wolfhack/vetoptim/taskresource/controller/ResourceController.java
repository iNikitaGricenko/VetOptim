package com.wolfhack.vetoptim.taskresource.controller;

import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.common.dto.ResourceDTO;
import com.wolfhack.vetoptim.taskresource.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    public List<Resource> getAllResources() {
        log.info("Fetching all resources");
        return resourceService.getAllResources();
    }

    @PostMapping
    public Resource createResource(@RequestBody Resource resource) {
        log.info("Creating new resource: {}", resource.getName());
        Resource createdResource = resourceService.createResource(resource);
        log.info("Resource created with ID: {}", createdResource.getId());
        return createdResource;
    }

    @PutMapping("/{id}")
    public Resource updateResource(@PathVariable Long id, @RequestBody ResourceDTO resourceDTO) {
        log.info("Updating resource with ID: {}", id);
        Resource updatedResource = resourceService.updateResource(id, resourceDTO);
        log.info("Resource updated with ID: {}", updatedResource.getId());
        return updatedResource;
    }

    @PatchMapping("/{id}")
    public Resource partialUpdateResource(@PathVariable Long id, @RequestBody ResourceDTO resourceDTO) {
        log.info("Partially updating resource with ID: {}", id);
        Resource updatedResource = resourceService.partialUpdateResource(id, resourceDTO);
        log.info("Resource partially updated with ID: {}", updatedResource.getId());
        return updatedResource;
    }

    @DeleteMapping("/{id}")
    public void deleteResource(@PathVariable Long id) {
        log.info("Deleting resource with ID: {}", id);
        resourceService.deleteResource(id);
        log.info("Resource deleted with ID: {}", id);
    }
}