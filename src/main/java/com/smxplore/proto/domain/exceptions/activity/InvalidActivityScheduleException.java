package com.smxplore.proto.domain.exceptions.activity;

public class InvalidActivityScheduleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidActivityScheduleException(String message) {
        super(message);
    }
}
