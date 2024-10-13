package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;

public interface IAppointmentTaskEventPublisher {

	void publishAppointmentTaskCreationEvent(AppointmentTaskCreationEvent event);

}
