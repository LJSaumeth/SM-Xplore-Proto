package com.smxplore.proto.domain.exceptions.attribute;

public class InvalidAttributeNameException extends RuntimeException {
    public InvalidAttributeNameException() {
        super("Attribute's name must not be blank.");
    }
}
