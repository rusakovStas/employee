package com.stasdev.backend.exceptions;

public class AdminDeleteForbidden extends RuntimeException {
    public AdminDeleteForbidden(String message) {
        super(message);
    }
}
