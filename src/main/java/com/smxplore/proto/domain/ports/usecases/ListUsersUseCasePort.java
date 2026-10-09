package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.User;

import java.util.List;

public interface ListUsersUseCasePort {

    List<User> handle();
}
