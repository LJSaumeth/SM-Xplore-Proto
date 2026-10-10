package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.User;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UpdateUserProfileUseCasePort {

    Mono<User> handle(UUID userId, String fullName, String email, String phone);
}
