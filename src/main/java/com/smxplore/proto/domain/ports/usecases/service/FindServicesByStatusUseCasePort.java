package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceStatus;
import reactor.core.publisher.Flux;

public interface FindServicesByStatusUseCasePort {
    Flux<Service> handle(ServiceStatus status);
}
