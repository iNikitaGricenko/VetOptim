package com.wolfhack.vetoptim.petmanagement.mapper;

import com.wolfhack.vetoptim.common.dto.pet.PetInteractionRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetInteractionResponseDTO;
import com.wolfhack.vetoptim.petmanagement.model.Pet;
import com.wolfhack.vetoptim.petmanagement.model.PetInteraction;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PetInteractionMapper {

	PetInteraction toModel(PetInteractionRequestDTO dto);

	PetInteractionResponseDTO toDTO(PetInteraction interaction);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
	PetInteraction updatePetFromDTO(PetInteractionRequestDTO interactionDetails, @MappingTarget PetInteraction interaction);

}