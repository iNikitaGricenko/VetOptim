package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.dto.ResourceDTO;
import com.wolfhack.vetoptim.taskresource.client.BillingClient;
import com.wolfhack.vetoptim.taskresource.mapper.ResourceMapper;
import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final TaskRepository taskRepository;
    private final BillingClient billingClient;

    public List<Resource> getAllResources() {
        log.info("Fetching all resources");
        return resourceRepository.findAll();
    }

    public Resource createResource(Resource resource) {
        log.info("Creating new resource: {}", resource.getName());
        Resource savedResource = resourceRepository.save(resource);
        log.info("Resource created with ID: {}", savedResource.getId());
        return savedResource;
    }

    public Resource updateResource(Long id, ResourceDTO resourceDTO) {
        log.info("Updating resource with ID: {}", id);
        Resource resource = resourceRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Resource not found with ID: {}", id);
                return new RuntimeException("Resource not found");
            });
        resource = resourceMapper.updateResourceFromDTO(resourceDTO, resource);
        Resource updatedResource = resourceRepository.save(resource);
        log.info("Resource updated successfully with ID: {}", updatedResource.getId());
        return updatedResource;
    }

    public Resource partialUpdateResource(Long id, ResourceDTO resourceDTO) {
        log.info("Partially updating resource with ID: {}", id);
        Resource resource = resourceRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Resource not found with ID: {}", id);
                return new RuntimeException("Resource not found");
            });
        resource = resourceMapper.partialUpdateResourceFromDTO(resourceDTO, resource);
        Resource updatedResource = resourceRepository.save(resource);
        log.info("Resource partially updated with ID: {}", updatedResource.getId());
        return updatedResource;
    }

    public void deleteResource(Long id) {
        log.info("Deleting resource with ID: {}", id);
        resourceRepository.deleteById(id);
        log.info("Resource deleted with ID: {}", id);
    }
}