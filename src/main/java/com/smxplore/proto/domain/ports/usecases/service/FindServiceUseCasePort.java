package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.Service;

import java.util.Optional;
import java.util.UUID;

public interface FindServiceUseCasePort {
    Optional<Service> handle(UUID serviceId);
}
