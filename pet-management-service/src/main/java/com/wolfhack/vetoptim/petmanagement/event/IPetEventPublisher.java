package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.pet.PetCreatedEvent;
import com.wolfhack.vetoptim.common.event.pet.PetDeletedEvent;
import com.wolfhack.vetoptim.common.event.pet.PetUpdatedEvent;

public interface IPetEventPublisher {

	void publishPetCreatedEvent(PetCreatedEvent event);

	void publishPetUpdatedEvent(PetUpdatedEvent event);

	void publishPetDeletedEvent(PetDeletedEvent event);

}
