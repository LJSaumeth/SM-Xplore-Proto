package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.ListUsersUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ListUsersUseCase implements ListUsersUseCasePort {

    private final UserRepositoryPort userRepository;

    @Override
    public Flux<User> handle() {
        return userRepository.findAll();
    }
}
