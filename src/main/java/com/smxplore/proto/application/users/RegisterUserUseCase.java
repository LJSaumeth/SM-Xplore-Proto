package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.exceptions.InvalidUserDataException;
import com.smxplore.proto.domain.exceptions.InvalidUserRoleException;
import com.smxplore.proto.domain.exceptions.UserEmailAlreadyRegisteredException;
import com.smxplore.proto.domain.exceptions.UserPhoneAlreadyRegisteredException;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.security.PasswordHasherPort;
import com.smxplore.proto.domain.ports.usecases.RegisterUserUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Set;

@RequiredArgsConstructor
public class RegisterUserUseCase implements RegisterUserUseCasePort {

    /** Roles a visitor can pick during public self-registration; they stay INACTIVE until an admin approves them. */
    private static final Set<UserRole> SELF_REGISTRABLE_ROLES = Set.of(UserRole.TOURIST, UserRole.SERVICE_PROVIDER,
            UserRole.GUIDE, UserRole.DESTINATION_MANAGER);

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;

    @Override
    public Mono<User> handle(User user, String rawPassword) {
        return Mono.justOrEmpty(user)
                .switchIfEmpty(Mono.error(new InvalidUserDataException("User is mandatory.")))
                .doOnNext(this::ensureSelfRegistrable)
                .flatMap(candidate -> passwordHasher.hash(rawPassword)
                        .map(hash -> {
                            candidate.changePasswordHash(hash);
                            return candidate;
                        }))
                .flatMap(this::ensureUnique)
                .flatMap(userRepository::save);
    }

    private void ensureSelfRegistrable(User user) {
        if (!SELF_REGISTRABLE_ROLES.contains(user.getRole())) {
            throw new InvalidUserRoleException(
                    "Role %s cannot be self-registered; an administrator must assign it."
                            .formatted(user.getRole()));
        }
    }

    private Mono<User> ensureUnique(User user) {
        return userRepository.existsByEmail(user.getEmail())
                .filter(emailExists -> !emailExists)
                .switchIfEmpty(Mono.error(new UserEmailAlreadyRegisteredException(user.getEmail())))
                .then(Mono.defer(() -> userRepository.existsByPhone(user.getPhone())))
                .filter(phoneExists -> !phoneExists)
                .switchIfEmpty(Mono.error(new UserPhoneAlreadyRegisteredException(user.getPhone())))
                .thenReturn(user);
    }
}
