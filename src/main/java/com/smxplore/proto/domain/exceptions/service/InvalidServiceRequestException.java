package com.smxplore.proto.domain.exceptions.service;

public class InvalidServiceRequestException extends RuntimeException {
    public InvalidServiceRequestException(String message) {
        super(message);
    }
}
