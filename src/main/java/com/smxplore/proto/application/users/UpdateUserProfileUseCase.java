package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.exceptions.UserEmailAlreadyRegisteredException;
import com.smxplore.proto.domain.exceptions.UserNotFoundException;
import com.smxplore.proto.domain.exceptions.UserPhoneAlreadyRegisteredException;
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
                .flatMap(user -> ensureContactDetailsAreFree(user, email, phone))
                .doOnNext(user -> user.updateProfile(fullName, email, phone))
                .flatMap(userRepository::save);
    }

    private Mono<User> ensureContactDetailsAreFree(User user, String email, String phone) {
        return ensureEmailIsFree(user, email)
                .then(ensurePhoneIsFree(user, phone))
                .thenReturn(user);
    }

    private Mono<Void> ensureEmailIsFree(User user, String email) {
        var normalized = email == null ? null : email.strip();
        if (normalized == null || normalized.equalsIgnoreCase(user.getEmail())) {
            return Mono.empty();
        }
        return userRepository.existsByEmail(normalized)
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new UserEmailAlreadyRegisteredException(normalized)))
                .then();
    }

    private Mono<Void> ensurePhoneIsFree(User user, String phone) {
        var normalized = phone == null ? null : phone.strip();
        if (normalized == null || normalized.equals(user.getPhone())) {
            return Mono.empty();
        }
        return userRepository.existsByPhone(normalized)
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new UserPhoneAlreadyRegisteredException(normalized)))
                .then();
    }
}
