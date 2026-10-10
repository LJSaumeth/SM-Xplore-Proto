package com.smxplore.proto.application.services;

import com.smxplore.proto.domain.exceptions.service.ServiceNotFoundException;
import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.service.ChangeServicePriceUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class ChangeServicePriceUseCase implements ChangeServicePriceUseCasePort {
    private final ServiceRepositoryPort serviceRepo;

    @Override
    public Mono<Service> handle(UUID serviceId, BigDecimal newPrice) {
        return serviceRepo.findById(serviceId)
                .switchIfEmpty(Mono.error(new ServiceNotFoundException(serviceId)))
                .doOnNext(service -> {
                    service.changePrice(newPrice);
                })
                .flatMap(serviceRepo::save);
    }
}
