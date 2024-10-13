package com.wolfhack.vetoptim.petmanagement.exception;

public class MedicalRecordNotFoundException extends RuntimeException {
    public MedicalRecordNotFoundException(Long id) {
        super("Medical record not found with ID: " + id);
    }
}