package com.smxplore.proto.domain.exceptions.service;

public class InvalidServiceProviderNameException extends RuntimeException {
    public InvalidServiceProviderNameException(String message) {
        super(message);
    }
}
