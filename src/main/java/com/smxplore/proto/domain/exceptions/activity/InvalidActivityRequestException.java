package com.smxplore.proto.domain.exceptions.activity;

public class InvalidActivityRequestException extends RuntimeException {
    public InvalidActivityRequestException(String message) {
        super(message);
    }
}
