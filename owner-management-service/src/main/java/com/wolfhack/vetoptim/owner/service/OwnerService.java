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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerService {

	private final OwnerRepository ownerRepository;
	private final OwnerMapper ownerMapper;
	private final AppointmentClient appointmentClient;
	private final OwnerEventPublisher ownerEventPublisher;

	public List<OwnerDTO> getAllOwners() {
		log.info("Fetching all owners");
		return ownerRepository.findAll()
			.stream()
			.map(ownerMapper::toDTO)
			.collect(Collectors.toList());
	}

	public Optional<OwnerDTO> getOwnerById(Long id) {
		log.info("Fetching owner by ID: {}", id);
		return ownerRepository.findById(id)
			.map(ownerMapper::toDTO);
	}

	public OwnerDTO createOwner(OwnerDTO ownerDTO) {
		log.info("Creating new owner with name: {}", ownerDTO.getName());

		Owner owner = ownerMapper.toModel(ownerDTO);
		Owner savedOwner = ownerRepository.save(owner);

		OwnerCreatedEvent event = new OwnerCreatedEvent(savedOwner.getId(), savedOwner.getName(), savedOwner.getContactDetails());
		ownerEventPublisher.publishOwnerCreatedEvent(event);

		log.info("Owner created with ID: {}", savedOwner.getId());
		return ownerMapper.toDTO(savedOwner);
	}

	public OwnerDTO updateOwner(Long id, OwnerDTO ownerDTO) {
		log.info("Updating owner with ID: {}", id);
		return ownerRepository.findById(id)
			.map(existingOwner -> {
				ownerMapper.updateOwnerFromDTO(ownerDTO, existingOwner);
				Owner updatedOwner = ownerRepository.save(existingOwner);
				OwnerUpdatedEvent event = new OwnerUpdatedEvent(updatedOwner.getId(), updatedOwner.getName(), updatedOwner.getContactDetails());
				ownerEventPublisher.publishOwnerUpdatedEvent(event);
				log.info("Owner updated with ID: {}", updatedOwner.getId());
				return ownerMapper.toDTO(updatedOwner);
			})
			.orElseThrow(() -> {
				log.error("Owner not found with ID: {}", id);
				return new RuntimeException("Owner not found");
			});
	}

	public void addPetToOwner(Long ownerId, Long petId) {
		log.info("Adding pet ID: {} to owner ID: {}", petId, ownerId);

		ownerRepository.findById(ownerId).ifPresent(owner -> {
			List<Long> petIds = owner.getPetIds();
			if (petIds == null) {
				petIds = new ArrayList<>();
			}
			petIds.add(petId);
			owner.setPetIds(petIds);
			ownerRepository.save(owner);

			log.info("Added pet ID: {} to owner ID: {}", petId, ownerId);
		});
	}

	public void deleteOwner(Long id) {
		log.info("Deleting owner with ID: {}", id);
		ownerRepository.findById(id).ifPresent(owner -> {
			ownerRepository.deleteById(id);
			OwnerDeletedEvent event = new OwnerDeletedEvent(owner.getId());
			ownerEventPublisher.publishOwnerDeletedEvent(event);
			log.info("Owner deleted with ID: {}", owner.getId());
		});
	}

}
