package com.smxplore.proto.domain.model.types;

import com.smxplore.proto.domain.exceptions.attribute.InvalidAttributeNameException;
import com.smxplore.proto.domain.exceptions.attribute.InvalidAttributeValueException;
import lombok.Builder;
import lombok.Setter;

import java.util.Objects;

@Builder
public final class Attribute {
    private final String name;
    private final Object value;

    public String name() {
        return name;
    }

    public Object value() {
        return value;
    }

    public void validate(){
        if (name == null || name.isBlank())
            throw new InvalidAttributeNameException();

        if (value == null)
            throw new InvalidAttributeValueException("Value is mandatory");

        boolean allowedType = value instanceof String || value instanceof Boolean
                || value instanceof Number || value instanceof Character || value instanceof Attribute;

        if (!allowedType)
            throw new InvalidAttributeValueException("Type not allowed");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Attribute attribute)) return false;
        return Objects.equals(name, attribute.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
