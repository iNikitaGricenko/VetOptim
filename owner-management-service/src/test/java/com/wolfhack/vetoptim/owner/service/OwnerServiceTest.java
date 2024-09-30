package com.wolfhack.vetoptim.owner.service;

import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import com.wolfhack.vetoptim.common.event.owner.OwnerCreatedEvent;
import com.wolfhack.vetoptim.common.event.owner.OwnerDeletedEvent;
import com.wolfhack.vetoptim.common.event.owner.OwnerUpdatedEvent;
import com.wolfhack.vetoptim.owner.event.OwnerEventPublisher;
import com.wolfhack.vetoptim.owner.mapper.OwnerMapper;
import com.wolfhack.vetoptim.owner.model.Owner;
import com.wolfhack.vetoptim.owner.repository.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OwnerServiceTest {

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private OwnerMapper ownerMapper;

	@Mock
	private OwnerEventPublisher ownerEventPublisher;

	@InjectMocks
	private OwnerService ownerService;

	@Test
	public void shouldGetAllOwners() {
		List<Owner> owners = List.of(new Owner(1L, "John Doe", "john@example.com", true, false, List.of(), List.of()));
		List<OwnerDTO> ownerDTOs = List.of(new OwnerDTO(1L, "John Doe", "john@example.com", true, false, List.of(), List.of()));

		when(ownerRepository.findAll()).thenReturn(owners);
		when(ownerMapper.toDTO(any(Owner.class))).thenReturn(ownerDTOs.get(0));

		List<OwnerDTO> result = ownerService.getAllOwners();

		assertEquals(1, result.size());
		assertEquals(ownerDTOs.get(0).getName(), result.get(0).getName());
		verify(ownerRepository).findAll();
	}

	@Test
	public void shouldCreateOwner() {
		OwnerDTO ownerDTO = new OwnerDTO(1L, "John Doe", "john@example.com", true, false, List.of(), List.of());
		Owner owner = new Owner(1L, "John Doe", "john@example.com", true, false, List.of(), List.of());

		when(ownerMapper.toModel(any(OwnerDTO.class))).thenReturn(owner);
		when(ownerRepository.save(any(Owner.class))).thenReturn(owner);
		when(ownerMapper.toDTO(any(Owner.class))).thenReturn(ownerDTO);

		OwnerDTO result = ownerService.createOwner(ownerDTO);

		assertEquals("John Doe", result.getName());
		verify(ownerEventPublisher).publishOwnerCreatedEvent(any(OwnerCreatedEvent.class));
		verify(ownerRepository).save(any(Owner.class));
	}

	@Test
	public void shouldUpdateOwner() {
		OwnerDTO ownerDTO = new OwnerDTO(1L, "John Doe", "john@example.com", true, false, List.of(), List.of());
		Owner existingOwner = new Owner(1L, "John Doe", "john@example.com", true, false, List.of(), List.of());

		when(ownerRepository.findById(anyLong())).thenReturn(Optional.of(existingOwner));
		when(ownerMapper.toDTO(any(Owner.class))).thenReturn(ownerDTO);
		when(ownerRepository.save(any(Owner.class))).thenReturn(existingOwner);

		OwnerDTO result = ownerService.updateOwner(1L, ownerDTO);

		assertEquals("John Doe", result.getName());
		verify(ownerEventPublisher).publishOwnerUpdatedEvent(any(OwnerUpdatedEvent.class));
	}

	@Test
	public void shouldDeleteOwner() {
		Owner owner = new Owner(1L, "John Doe", "john@example.com", true, false, List.of(), List.of());

		when(ownerRepository.findById(anyLong())).thenReturn(Optional.of(owner));

		ownerService.deleteOwner(1L);

		verify(ownerRepository).deleteById(1L);
		verify(ownerEventPublisher).publishOwnerDeletedEvent(any(OwnerDeletedEvent.class));
	}

	@Test
	void shouldAddPetToOwner() {
		Owner owner = new Owner(1L, "John Doe", "john@example.com", true, false, List.of(), List.of());
		Long petId = 100L;

		when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));

		ownerService.addPetToOwner(1L, petId);

		assertTrue(owner.getPetIds().contains(petId));
		verify(ownerRepository, times(1)).save(owner);
	}
}
