package com.smxplore.proto.domain.exceptions.event;

public class InvalidEventRequestException extends RuntimeException {
    public InvalidEventRequestException(String message) {
        super(message);
    }
}
