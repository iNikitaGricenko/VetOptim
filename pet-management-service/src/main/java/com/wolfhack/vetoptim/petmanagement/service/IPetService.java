package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.common.dto.pet.PetDTO;

import java.util.List;
import java.util.Optional;

public interface IPetService {

	List<PetDTO> getAllPets();

	Optional<PetDTO> getPetById(Long id);

	List<PetDTO> getAllPetsByOwnerId(Long ownerId);

	PetDTO createPet(PetDTO petDTO);

	PetDTO updatePet(Long id, PetDTO petDetails);

	void updateOwnerInfoForPets(Long ownerId, String ownerName);

	void deletePet(Long id);

	void handleAppointmentCreated(AppointmentDTO appointmentDTO);

	void handleAppointmentUpdated(AppointmentDTO appointmentDTO);

}
