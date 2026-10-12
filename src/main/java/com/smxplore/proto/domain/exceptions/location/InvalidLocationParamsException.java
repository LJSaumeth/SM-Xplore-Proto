package com.smxplore.proto.domain.exceptions.location;

public class InvalidLocationParamsException extends RuntimeException {
    public InvalidLocationParamsException() {
        super("Location needs latitude and longitude information.");
    }
}
