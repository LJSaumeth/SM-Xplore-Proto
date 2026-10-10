package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.types.Attribute;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

public interface AddAttributeToServiceUseCasePort {
    Mono<Service> handle(UUID reportId, Collection<Attribute> attributes);
}
