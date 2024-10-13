package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.pet.VaccinationRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.VaccinationResponseDTO;

import java.util.List;

public interface IVaccinationService {

	List<VaccinationResponseDTO> getVaccinationsForPet(Long petId);

	VaccinationResponseDTO createVaccination(Long petId, VaccinationRequestDTO vaccinationRequestDTO);

	VaccinationResponseDTO updateVaccination(Long vaccinationId, VaccinationRequestDTO vaccinationRequestDTO);

	void deleteVaccination(Long vaccinationId);

}
