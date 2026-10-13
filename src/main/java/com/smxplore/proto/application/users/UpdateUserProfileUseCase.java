package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.exceptions.UserNotFoundException;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.UpdateUserProfileUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UpdateUserProfileUseCase implements UpdateUserProfileUseCasePort {

    private final UserRepositoryPort userRepository;

    @Override
    public Mono<User> handle(UUID userId, String fullName, String email, String phone) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .doOnNext(user -> user.updateProfile(fullName, email, phone))
                .flatMap(userRepository::save);
    }
}
