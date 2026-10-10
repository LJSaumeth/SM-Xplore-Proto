package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.User;

import reactor.core.publisher.Mono;

public interface RegisterUserUseCasePort {

    Mono<User> handle(User user);
}
