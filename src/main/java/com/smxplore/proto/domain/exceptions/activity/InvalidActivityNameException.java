package com.smxplore.proto.domain.exceptions.activity;

public class InvalidActivityNameException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidActivityNameException(String message) {
        super(message);
    }
}
