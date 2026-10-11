package com.smxplore.proto.domain.exceptions.event;

public class InvalidEventStatusTransitionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidEventStatusTransitionException(String message) {
        super(message);
    }
}
