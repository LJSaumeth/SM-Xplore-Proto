package com.smxplore.proto.domain.exceptions.event;

public class InvalidEventStatusTransitionException extends RuntimeException {
    public InvalidEventStatusTransitionException(String message) {
        super(message);
    }
}
