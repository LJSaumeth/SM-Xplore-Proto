package com.smxplore.proto.domain.exceptions;

public class UserEmailAlreadyRegisteredException extends RuntimeException {

    public UserEmailAlreadyRegisteredException(String email) {
        super("The email '" + email + "' is already registered.");
    }
}
