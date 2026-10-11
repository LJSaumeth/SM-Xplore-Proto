package com.smxplore.proto.domain.exceptions.activity;

public class InvalidActivityStatusTransitionException extends RuntimeException {
    public InvalidActivityStatusTransitionException(String message) {
        super(message);
    }
}
