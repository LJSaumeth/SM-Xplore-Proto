package com.smxplore.proto.domain.exceptions.activity;

public class InvalidActivityStatusTransitionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidActivityStatusTransitionException(String message) {
        super(message);
    }
}
