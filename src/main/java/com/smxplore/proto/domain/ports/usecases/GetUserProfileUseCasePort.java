package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.User;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GetUserProfileUseCasePort {

    Mono<User> handle(UUID userId);
}
