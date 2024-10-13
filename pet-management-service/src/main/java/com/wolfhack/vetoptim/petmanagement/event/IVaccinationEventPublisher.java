package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.vaccination.VaccinationReminderEvent;

public interface IVaccinationEventPublisher {

	void publishVaccinationReminderEvent(VaccinationReminderEvent event);

}
