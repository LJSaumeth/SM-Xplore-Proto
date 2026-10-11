package com.smxplore.proto.domain.exceptions.event;

public class InvalidEventScheduleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidEventScheduleException(String message) {
        super(message);
    }
}
