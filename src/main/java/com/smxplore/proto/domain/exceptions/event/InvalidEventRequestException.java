package com.smxplore.proto.domain.exceptions.event;

public class InvalidEventRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidEventRequestException(String message) {
        super(message);
    }
}
