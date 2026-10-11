package com.smxplore.proto.domain.exceptions.activity;

public class InvalidActivityRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidActivityRequestException(String message) {
        super(message);
    }
}
