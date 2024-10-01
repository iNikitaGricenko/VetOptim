package com.wolfhack.vetoptim.owner.service;

import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import com.wolfhack.vetoptim.common.event.owner.OwnerCreatedEvent;
import com.wolfhack.vetoptim.common.event.owner.OwnerDeletedEvent;
import com.wolfhack.vetoptim.common.event.owner.OwnerUpdatedEvent;
import com.wolfhack.vetoptim.owner.client.AppointmentClient;
import com.wolfhack.vetoptim.owner.event.OwnerEventPublisher;
import com.wolfhack.vetoptim.owner.mapper.OwnerMapper;
import com.wolfhack.vetoptim.owner.model.Owner;
import com.wolfhack.vetoptim.owner.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private OwnerMapper ownerMapper;

	@Mock
	private AppointmentClient appointmentClient;

	@Mock
	private OwnerEventPublisher ownerEventPublisher;

	@InjectMocks
	private OwnerService ownerService;

	private Owner owner;
	private OwnerDTO ownerDTO;

	@BeforeEach
	void setUp() {
		owner = new Owner();
		owner.setId(1L);
		owner.setName("John Doe");

		ownerDTO = new OwnerDTO();
		ownerDTO.setId(1L);
		ownerDTO.setName("John Doe");
	}

	@Test
	void testGetOwnerById_Success() {
		when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
		when(ownerMapper.toDTO(owner)).thenReturn(ownerDTO);

		Optional<OwnerDTO> result = ownerService.getOwnerById(1L);

		assertEquals(Optional.of(ownerDTO), result);
		verify(ownerRepository).findById(1L);
		verify(ownerMapper).toDTO(owner);
	}

	@Test
	void testCreateOwner_Success() {
		when(ownerMapper.toModel(ownerDTO)).thenReturn(owner);
		when(ownerRepository.save(owner)).thenReturn(owner);
		when(ownerMapper.toDTO(owner)).thenReturn(ownerDTO);

		OwnerDTO result = ownerService.createOwner(ownerDTO);

		assertEquals(ownerDTO, result);
		verify(ownerRepository).save(owner);
		verify(ownerEventPublisher).publishOwnerCreatedEvent(any(OwnerCreatedEvent.class));
	}

	@Test
	void testUpdateOwner_Success() {
		when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
		when(ownerRepository.save(any(Owner.class))).thenReturn(owner);
		when(ownerMapper.toDTO(any(Owner.class))).thenReturn(ownerDTO);

		OwnerDTO updatedOwnerDTO = ownerService.updateOwner(1L, ownerDTO);

		assertEquals(ownerDTO, updatedOwnerDTO);
		verify(ownerRepository).findById(1L);
		verify(ownerRepository).save(any(Owner.class));
		verify(ownerEventPublisher).publishOwnerUpdatedEvent(any(OwnerUpdatedEvent.class));
	}

	@Test
	void testUpdateOwner_NotFound() {
		when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> ownerService.updateOwner(1L, ownerDTO));
	}

	@Test
	void testDeleteOwner_Success() {
		when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));

		ownerService.deleteOwner(1L);

		verify(ownerRepository).deleteById(1L);
		verify(ownerEventPublisher).publishOwnerDeletedEvent(any(OwnerDeletedEvent.class));
	}

	@Test
	void testAddPetToOwner_Success() {
		when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));

		ownerService.addPetToOwner(1L, 100L);

		assertEquals(1, owner.getPetIds().size());
		assertEquals(100L, owner.getPetIds().get(0));
		verify(ownerRepository).save(owner);
	}
}