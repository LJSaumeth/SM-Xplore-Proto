package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.LoginResult;

import reactor.core.publisher.Mono;

public interface LoginUseCasePort {

    Mono<LoginResult> handle(String email, String rawPassword);
}
