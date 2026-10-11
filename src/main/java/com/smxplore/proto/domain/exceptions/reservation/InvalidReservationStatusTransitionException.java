package com.smxplore.proto.domain.exceptions.reservation;

public class InvalidReservationStatusTransitionException extends RuntimeException {
    public InvalidReservationStatusTransitionException(String message) {
        super(message);
    }
}
