package com.smxplore.proto.domain.exceptions.reservation;

public class ReservationItemsNotAllowedException extends RuntimeException {
    public ReservationItemsNotAllowedException(String message) {
        super(message);
    }
}
