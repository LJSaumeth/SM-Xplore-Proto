package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.exceptions.UserNotFoundException;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.GetUserProfileUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class GetUserProfileUseCase implements GetUserProfileUseCasePort {

    private final UserRepositoryPort userRepository;

    @Override
    public Mono<User> handle(UUID userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)));
    }
}
