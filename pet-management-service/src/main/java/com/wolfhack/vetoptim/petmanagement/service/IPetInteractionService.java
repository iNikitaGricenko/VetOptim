package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.pet.PetInteractionRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetInteractionResponseDTO;

import java.util.List;

public interface IPetInteractionService {

	List<PetInteractionResponseDTO> getPetInteractions(Long petId);

	PetInteractionResponseDTO logInteraction(Long petId, PetInteractionRequestDTO interactionRequest);

}
