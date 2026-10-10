package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

public interface AddServicePhotosUseCasePort {
    Mono<Service> handle(UUID reportId, Collection<String> photos);
}
