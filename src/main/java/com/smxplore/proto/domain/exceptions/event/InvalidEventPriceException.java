package com.smxplore.proto.domain.exceptions.event;

public class InvalidEventPriceException extends RuntimeException {
    public InvalidEventPriceException(String message) {
        super(message);
    }
}
