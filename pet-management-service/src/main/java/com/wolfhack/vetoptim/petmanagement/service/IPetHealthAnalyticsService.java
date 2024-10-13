package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.pet.PetHealthSummary;

public interface IPetHealthAnalyticsService {

	PetHealthSummary getPetHealthSummary(Long petId);

}
