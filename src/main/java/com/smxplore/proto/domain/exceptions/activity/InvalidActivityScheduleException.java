package com.smxplore.proto.domain.exceptions.activity;

import java.io.Serial;

public class InvalidActivityScheduleException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidActivityScheduleException(String message) {
        super(message);
    }
}
