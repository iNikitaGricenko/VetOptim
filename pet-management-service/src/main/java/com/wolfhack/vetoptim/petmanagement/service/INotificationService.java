package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;

public interface INotificationService {

	void notifyOwnerOfAppointment(AppointmentDTO appointment);

	void notifyOfResourceDepletion(String resourceName, int remainingQuantity);

}
