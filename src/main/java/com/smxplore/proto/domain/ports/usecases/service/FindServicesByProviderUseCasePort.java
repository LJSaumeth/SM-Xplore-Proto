package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface FindServicesByProviderUseCasePort {
    Flux<Service> handle(UUID providerId);
}
