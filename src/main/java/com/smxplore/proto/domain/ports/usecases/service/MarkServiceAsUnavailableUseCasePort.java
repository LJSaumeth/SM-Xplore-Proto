package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MarkServiceAsUnavailableUseCasePort {
    Mono<Service> handle(UUID serviceId);
}
