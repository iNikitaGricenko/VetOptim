package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.dto.ResourceDTO;
import com.wolfhack.vetoptim.taskresource.mapper.ResourceMapper;
import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceMapper resourceMapper;

    @InjectMocks
    private ResourceService resourceService;

	private AutoCloseable openedMocks;

	@BeforeEach
    void setUp() {
		openedMocks = MockitoAnnotations.openMocks(this);
    }

	@AfterEach
	void tearDown() throws Exception {
        openedMocks.close();
    }

    @Test
    void testGetAllResources() {
        resourceService.getAllResources();
        verify(resourceRepository).findAll();
    }

    @Test
    void testCreateResource() {
        Resource resource = new Resource();
        when(resourceRepository.save(any())).thenReturn(resource);

        Resource createdResource = resourceService.createResource(resource);

        assertNotNull(createdResource);
        verify(resourceRepository).save(resource);
    }

    @Test
    void testUpdateResource() {
        Long id = 1L;
        ResourceDTO resourceDTO = new ResourceDTO();
        Resource resource = new Resource();
        when(resourceRepository.findById(id)).thenReturn(Optional.of(resource));
        when(resourceRepository.save(any())).thenReturn(resource);

        Resource updatedResource = resourceService.updateResource(id, resourceDTO);

        assertNotNull(updatedResource);
        verify(resourceMapper).updateResourceFromDTO(resourceDTO, resource);
        verify(resourceRepository).save(resource);
    }

    @Test
    void testPartialUpdateResource() {
        Long id = 1L;
        ResourceDTO resourceDTO = new ResourceDTO();
        Resource resource = new Resource();
        when(resourceRepository.findById(id)).thenReturn(Optional.of(resource));
        when(resourceRepository.save(any())).thenReturn(resource);

        Resource partiallyUpdatedResource = resourceService.partialUpdateResource(id, resourceDTO);

        assertNotNull(partiallyUpdatedResource);
        verify(resourceMapper).partialUpdateResourceFromDTO(resourceDTO, resource);
        verify(resourceRepository).save(resource);
    }

    @Test
    void testDeleteResource() {
        Long id = 1L;
        resourceService.deleteResource(id);
        verify(resourceRepository).deleteById(id);
    }
}