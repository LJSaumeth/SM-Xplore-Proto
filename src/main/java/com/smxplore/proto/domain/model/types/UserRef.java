package com.smxplore.proto.domain.model.types;

import com.smxplore.proto.domain.exceptions.securityreport.InvalidUserReporterNameException;

import java.util.UUID;

public record UserRef(UUID id, String name) {
    public void  validate() {
        if (id == null) throw new NullPointerException("id is null");
        if (name == null || name.isBlank())
            throw new InvalidUserReporterNameException("User name is mandatory");
    }
}
