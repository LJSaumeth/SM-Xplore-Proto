package com.smxplore.proto.domain.exceptions.reservation;

public class InvalidBookingDateException extends RuntimeException {
    public InvalidBookingDateException(String message) {
        super(message);
    }
}
