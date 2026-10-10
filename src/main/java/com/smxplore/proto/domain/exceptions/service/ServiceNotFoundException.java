package com.smxplore.proto.domain.exceptions.service;

import java.util.UUID;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(UUID serviceId) {
        super("Service with id %s not found.".formatted(serviceId.toString()));
    }
}
