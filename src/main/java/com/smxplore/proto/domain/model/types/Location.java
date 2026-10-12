package com.smxplore.proto.domain.model.types;

import com.smxplore.proto.domain.exceptions.location.InvalidLocationParamsException;

public record Location(Double latitude, Double longitude) {
    public void validate() {
        if (latitude == null || longitude == null) {
            throw new InvalidLocationParamsException();
        }
    }
}
