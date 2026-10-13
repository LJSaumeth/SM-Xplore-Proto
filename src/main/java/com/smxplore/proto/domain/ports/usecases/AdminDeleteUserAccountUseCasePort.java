package com.smxplore.proto.domain.ports.usecases;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AdminDeleteUserAccountUseCasePort {

    Mono<Void> handle(UUID userId);
}
