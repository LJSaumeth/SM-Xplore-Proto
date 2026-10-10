package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AssignUserRoleUseCasePort {

    Mono<User> handle(UUID userId, UserRole newRole);
}
