package com.smxplore.proto.domain.exceptions.event;

public class InvalidExpectedAttendanceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidExpectedAttendanceException(String message) {
        super(message);
    }
}
