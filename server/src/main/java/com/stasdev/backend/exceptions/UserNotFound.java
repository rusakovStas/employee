package com.stasdev.backend.exceptions;

public class UserNotFound extends RuntimeException{
    public UserNotFound(String message) {
        super(message);
    }

    public UserNotFound() {
    }
}
