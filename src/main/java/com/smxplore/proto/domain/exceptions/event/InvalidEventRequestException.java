package com.smxplore.proto.domain.exceptions.event;

import java.io.Serial;

public class InvalidEventRequestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidEventRequestException(String message) {
        super(message);
    }
}
