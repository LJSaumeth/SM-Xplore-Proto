package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.exceptions.UserNotFoundException;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.security.PasswordHasherPort;
import com.smxplore.proto.domain.ports.usecases.ResetUserPasswordUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class ResetUserPasswordUseCase implements ResetUserPasswordUseCasePort {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;

    @Override
    public Mono<Void> handle(UUID userId, String rawPassword) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .flatMap(user -> passwordHasher.hash(rawPassword)
                        .map(hash -> {
                            user.changePasswordHash(hash);
                            return user;
                        }))
                .flatMap(userRepository::save)
                .then();
    }
}
