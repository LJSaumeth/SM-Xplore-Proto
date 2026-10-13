package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.exceptions.UserNotFoundException;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.AdminDeleteUserAccountUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class AdminDeleteUserAccountUseCase implements AdminDeleteUserAccountUseCasePort {

    private final UserRepositoryPort userRepository;

    @Override
    public Mono<Void> handle(UUID userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .flatMap(user -> userRepository.deleteById(userId));
    }
}
