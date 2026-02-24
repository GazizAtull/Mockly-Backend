package com.mockly.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String idOrEmail) {
        super("User not found: " + idOrEmail);
    }
}
