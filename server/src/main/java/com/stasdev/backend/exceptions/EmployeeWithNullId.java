package com.stasdev.backend.exceptions;

public class EmployeeWithNullId extends RuntimeException {
    public EmployeeWithNullId(String message) {
        super(message);
    }
}
