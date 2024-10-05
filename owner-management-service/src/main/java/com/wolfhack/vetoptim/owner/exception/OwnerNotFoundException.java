package com.wolfhack.vetoptim.owner.exception;

public class OwnerNotFoundException extends RuntimeException {
    public OwnerNotFoundException(Long id) {
        super("Owner not found with ID: " + id);
    }
}