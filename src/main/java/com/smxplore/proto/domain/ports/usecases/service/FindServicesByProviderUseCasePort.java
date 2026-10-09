package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.Service;

import java.util.List;
import java.util.UUID;

public interface FindServicesByProviderUseCasePort {
    List<Service> handle(UUID providerId);
}
