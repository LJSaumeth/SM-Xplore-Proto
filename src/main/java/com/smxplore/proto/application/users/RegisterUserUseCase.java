package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.exceptions.InvalidUserDataException;
import com.smxplore.proto.domain.exceptions.UserEmailAlreadyRegisteredException;
import com.smxplore.proto.domain.exceptions.UserPhoneAlreadyRegisteredException;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.security.PasswordHasherPort;
import com.smxplore.proto.domain.ports.usecases.RegisterUserUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase implements RegisterUserUseCasePort {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;

    @Override
    public Mono<User> handle(User user, String rawPassword) {
        return Mono.justOrEmpty(user)
                .switchIfEmpty(Mono.error(new InvalidUserDataException("User is mandatory.")))
                .flatMap(candidate -> passwordHasher.hash(rawPassword)
                        .map(hash -> {
                            candidate.changePasswordHash(hash);
                            return candidate;
                        }))
                .flatMap(this::ensureUnique)
                .flatMap(userRepository::save);
    }

    private Mono<User> ensureUnique(User user) {
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(new UserEmailAlreadyRegisteredException(user.getEmail()));
                    }
                    return userRepository.existsByPhone(user.getPhone());
                })
                .flatMap(phoneExists -> {
                    if (phoneExists) {
                        return Mono.error(new UserPhoneAlreadyRegisteredException(user.getPhone()));
                    }
                    return Mono.just(user);
                });
    }
}
