package com.wolfhack.vetoptim.appointment.exception;


public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(String message) {
        super(message);
    }
}