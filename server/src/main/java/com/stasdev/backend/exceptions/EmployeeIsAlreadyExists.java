package com.stasdev.backend.exceptions;

public class EmployeeIsAlreadyExists extends RuntimeException {
    public EmployeeIsAlreadyExists(String message) {
        super(message);
    }
}
