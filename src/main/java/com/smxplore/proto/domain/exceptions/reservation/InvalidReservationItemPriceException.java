package com.smxplore.proto.domain.exceptions.reservation;

public class InvalidReservationItemPriceException extends RuntimeException {
    public InvalidReservationItemPriceException(String message) {
        super(message);
    }
}
