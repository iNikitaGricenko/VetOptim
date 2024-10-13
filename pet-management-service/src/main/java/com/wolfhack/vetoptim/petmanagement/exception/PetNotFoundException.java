package com.wolfhack.vetoptim.petmanagement.exception;

public class PetNotFoundException extends RuntimeException {
    public PetNotFoundException(Long id) {
        super("Pet not found with ID: " + id);
    }
}