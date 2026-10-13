package com.smxplore.proto.domain.exceptions;

/**
 * Base class for all business rule violations raised by the domain layer.
 * Unchecked by team convention.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
