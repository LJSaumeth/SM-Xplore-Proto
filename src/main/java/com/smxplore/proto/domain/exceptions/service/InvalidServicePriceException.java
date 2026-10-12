package com.smxplore.proto.domain.exceptions.service;

public class InvalidServicePriceException extends RuntimeException {
    public InvalidServicePriceException(String message) {
        super(message);
    }
}
