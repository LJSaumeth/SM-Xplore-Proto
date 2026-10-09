package com.smxplore.proto.domain.model.types;

import com.smxplore.proto.domain.exceptions.service.InvalidServiceProviderNameException;

import java.util.UUID;

public record ProviderRef(UUID id, String name) {
    public void validate() {
        if (id == null) throw new NullPointerException("id is null");
        if (name == null) throw new InvalidServiceProviderNameException("Provider name is null");
    }
}
