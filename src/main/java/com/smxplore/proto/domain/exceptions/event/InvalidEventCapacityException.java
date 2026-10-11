package com.smxplore.proto.domain.exceptions.event;

public class InvalidEventCapacityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidEventCapacityException(String message) {
        super(message);
    }
}
