package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.dto.ResourceDTO;
import com.wolfhack.vetoptim.taskresource.mapper.ResourceMapper;
import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceMapper resourceMapper;

    @InjectMocks
    private ResourceService resourceService;

    private Resource resource;
    private ResourceDTO resourceDTO;

    @BeforeEach
    void setUp() {
        resource = new Resource();
        resource.setId(1L);
        resource.setName("Vaccine");
        resource.setQuantity(10);

        resourceDTO = new ResourceDTO();
        resourceDTO.setId(1L);
        resourceDTO.setName("Vaccine Updated");
    }

    @Test
    void getAllResources_Success() {
        resourceService.getAllResources();

        verify(resourceRepository).findAll();
    }

    @Test
    void createResource_Success() {
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        when(resourceMapper.toModel(any(ResourceDTO.class))).thenReturn(resource);
        when(resourceMapper.toDTO(any(Resource.class))).thenReturn(resourceDTO);

        ResourceDTO createdResource = resourceService.createResource(resourceDTO);

        verify(resourceRepository).save(resource);
        assertEquals("Vaccine Updated", createdResource.getName());
    }

    @Test
    void updateResource_Success() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(resourceMapper.updateResourceFromDTO(resourceDTO, resource)).thenReturn(resource);
        when(resourceRepository.save(resource)).thenReturn(resource);
        when(resourceMapper.toDTO(resource)).thenReturn(resourceDTO);

        ResourceDTO updatedResource = resourceService.updateResource(1L, resourceDTO);

        verify(resourceRepository).save(resource);
        assertNotNull(updatedResource);
        assertEquals(resource.getId(), updatedResource.getId());
    }

    @Test
    void updateResource_ResourceNotFound() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> resourceService.updateResource(1L, resourceDTO));
    }

    @Test
    void partialUpdateResource_Success() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(resourceMapper.partialUpdateResourceFromDTO(resourceDTO, resource)).thenReturn(resource);
        when(resourceRepository.save(resource)).thenReturn(resource);
        when(resourceMapper.toDTO(resource)).thenReturn(resourceDTO);

        ResourceDTO updatedResource = resourceService.partialUpdateResource(1L, resourceDTO);

        verify(resourceRepository).save(resource);
        assertNotNull(updatedResource);
        assertEquals(resource.getId(), updatedResource.getId());
    }

    @Test
    void partialUpdateResource_ResourceNotFound() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> resourceService.partialUpdateResource(1L, resourceDTO));
    }

    @Test
    void deleteResource_Success() {
        resourceService.deleteResource(1L);

        verify(resourceRepository).deleteById(1L);
    }
}