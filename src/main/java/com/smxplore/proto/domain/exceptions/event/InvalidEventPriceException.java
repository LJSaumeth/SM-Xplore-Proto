package com.smxplore.proto.domain.exceptions.event;

public class InvalidEventPriceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidEventPriceException(String message) {
        super(message);
    }
}
