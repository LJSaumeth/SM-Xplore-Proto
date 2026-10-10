package com.smxplore.proto.domain.ports.usecases;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ResetUserPasswordUseCasePort {

    Mono<Void> handle(UUID userId, String newPasswordHash);
}
