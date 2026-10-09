package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.User;

import java.util.UUID;

public interface GetUserProfileUseCasePort {

    User handle(UUID userId);
}
