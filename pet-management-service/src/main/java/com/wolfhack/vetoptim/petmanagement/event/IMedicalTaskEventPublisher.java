package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.task.MedicalTaskCreationEvent;

public interface IMedicalTaskEventPublisher {

	void publishMedicalTaskCreationEvent(MedicalTaskCreationEvent event);

}
