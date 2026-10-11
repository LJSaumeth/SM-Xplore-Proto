package com.smxplore.proto.domain.exceptions.event;

public class InvalidEventNameException extends RuntimeException {
    public InvalidEventNameException(String message) {
        super(message);
    }
}
