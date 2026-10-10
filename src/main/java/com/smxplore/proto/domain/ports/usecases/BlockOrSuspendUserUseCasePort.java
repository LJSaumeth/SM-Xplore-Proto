package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserStatus;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BlockOrSuspendUserUseCasePort {

    Mono<User> handle(UUID userId, UserStatus targetStatus);
}
