package com.smxplore.proto.domain.exceptions.reservation;

public class DifferentProvidersInReservationException extends RuntimeException {
    public DifferentProvidersInReservationException(String message) {
        super(message);
    }
}
