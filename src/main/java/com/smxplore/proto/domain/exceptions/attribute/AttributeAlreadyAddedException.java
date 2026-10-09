package com.smxplore.proto.domain.exceptions.attribute;

public class AttributeAlreadyAddedException extends RuntimeException {
    public AttributeAlreadyAddedException() {
        super("Attribute already exists!");
    }
}
