package com.smxplore.proto.domain.exceptions.reservation;

public class InvalidReservationTotalException extends RuntimeException {
    public InvalidReservationTotalException(String message) {
        super(message);
    }
}
