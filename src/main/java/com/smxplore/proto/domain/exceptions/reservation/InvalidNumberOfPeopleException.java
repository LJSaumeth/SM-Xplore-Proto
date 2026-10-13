package com.smxplore.proto.domain.exceptions.reservation;

public class InvalidNumberOfPeopleException extends RuntimeException {
    public InvalidNumberOfPeopleException(String message) {
        super(message);
    }
}
