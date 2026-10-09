package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.types.ProviderRef;

import java.util.Optional;
import java.util.UUID;

public interface RegisterServiceUseCasePort {
    Optional<UUID> handle(ProviderRef provider, Service service);
}
