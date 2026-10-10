package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceStatus;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ChangeServiceStatusUseCasePort {
    Mono<Service> handle(UUID serviceId, ServiceStatus newStatus);
}
