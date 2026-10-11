package com.smxplore.proto.domain.exceptions.event;

public class InvalidEventNameException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidEventNameException(String message) {
        super(message);
    }
}
