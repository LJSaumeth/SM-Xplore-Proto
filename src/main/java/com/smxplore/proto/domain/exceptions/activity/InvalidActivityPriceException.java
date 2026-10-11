package com.smxplore.proto.domain.exceptions.activity;

public class InvalidActivityPriceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidActivityPriceException(String message) {
        super(message);
    }
}
