package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.service.ServiceType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface ServiceRepositoryPort {
    Mono<Service> save(Service service);

    boolean changeStatus(UUID serviceId, ServiceStatus newStatus);

    Flux<Service> findAllByStatusAndType(ServiceStatus status, ServiceType type);

    Mono<Service> findById(UUID id);

    Flux<Service> findAllByStatus(ServiceStatus status);

    Flux<Service> findAllByProviderId(UUID providerId);

    Flux<Service> findAllByProviderName(String providerName);

    Flux<Service> findAll();

    boolean changePrice(UUID serviceId, BigDecimal newPrice);
}
