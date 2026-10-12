package com.smxplore.proto.domain.exceptions.service;

public class InvalidServiceNameException extends RuntimeException {
    public InvalidServiceNameException(String message) {
        super(message);
    }
}
