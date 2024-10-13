package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.task.EmergencyTaskCreationEvent;

public interface IEmergencyTaskEventPublisher {

	void publishEmergencyTaskCreationEvent(EmergencyTaskCreationEvent event);

}
