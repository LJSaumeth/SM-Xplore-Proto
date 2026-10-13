package com.smxplore.proto.domain.exceptions;

/**
 * Raised when a {@code Site} would violate one of its invariants
 * (identifier, zone reference, name, coordinates or photo URLs).
 */
public class InvalidSiteException extends DomainException {

    public InvalidSiteException(String message) {
        super(message);
    }
}
