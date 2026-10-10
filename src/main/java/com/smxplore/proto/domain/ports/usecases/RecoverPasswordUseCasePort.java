package com.smxplore.proto.domain.ports.usecases;

import reactor.core.publisher.Mono;

public interface RecoverPasswordUseCasePort {

    Mono<Void> handle(String email);
}
