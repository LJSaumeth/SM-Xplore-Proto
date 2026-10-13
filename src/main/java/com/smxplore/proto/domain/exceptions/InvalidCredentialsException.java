package com.smxplore.proto.domain.exceptions;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("The provided credentials are invalid.");
    }
}
