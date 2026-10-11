package com.smxplore.proto.domain.exceptions.activity;

public class InvalidActivityCapacityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidActivityCapacityException(String message) {
        super(message);
    }
}
