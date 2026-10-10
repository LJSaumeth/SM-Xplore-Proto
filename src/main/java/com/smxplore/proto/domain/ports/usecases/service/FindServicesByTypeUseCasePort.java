package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.service.ServiceType;
import reactor.core.publisher.Flux;

public interface FindServicesByTypeUseCasePort {
    Flux<Service> findAllByType(ServiceType type, ServiceStatus status);
}
