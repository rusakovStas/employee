package com.stasdev.backend.exceptions;

public class UserIsAlreadyExist extends RuntimeException {
    public UserIsAlreadyExist(String message) {
        super(message);
    }
}
