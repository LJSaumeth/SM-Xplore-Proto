package com.smxplore.proto.domain.exceptions;

/**
 * Raised when a {@code Zone} would violate one of its invariants
 * (identifier or name).
 */
public class InvalidZoneException extends DomainException {

    public InvalidZoneException(String message) {
        super(message);
    }
}
