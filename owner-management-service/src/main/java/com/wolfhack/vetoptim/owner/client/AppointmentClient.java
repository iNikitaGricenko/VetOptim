package com.wolfhack.vetoptim.owner.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "appointment-service", url = "${appointment.service.url}")
public interface AppointmentClient {

    @GetMapping("/appointments/owner/{ownerId}")
    List<Long> getAppointmentsForOwner(@PathVariable("ownerId") Long ownerId);
}