package com.smxplore.proto.domain.exceptions.reservation;

public class ReservationWithoutItemsException extends RuntimeException {
    public ReservationWithoutItemsException(String message) {
        super(message);
    }
}
