package com.smxplore.proto.domain.exceptions.event;

import java.io.Serial;

public class InvalidEventScheduleException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidEventScheduleException(String message) {
        super(message);
    }
}
