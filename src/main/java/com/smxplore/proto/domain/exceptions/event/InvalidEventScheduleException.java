package com.smxplore.proto.domain.exceptions.event;

public class InvalidEventScheduleException extends RuntimeException {
    public InvalidEventScheduleException(String message) {
        super(message);
    }
}
