package com.wolfhack.vetoptim.petmanagement.exception;

public class OwnerNotFoundException extends RuntimeException {
    public OwnerNotFoundException(Long id) {
        super("Owner not found with ID: " + id);
    }
}