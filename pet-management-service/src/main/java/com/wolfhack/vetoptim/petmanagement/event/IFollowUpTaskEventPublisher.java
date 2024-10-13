package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.task.FollowUpTaskCreationEvent;

public interface IFollowUpTaskEventPublisher {

	void publishFollowUpTaskCreationEvent(FollowUpTaskCreationEvent event);

}
