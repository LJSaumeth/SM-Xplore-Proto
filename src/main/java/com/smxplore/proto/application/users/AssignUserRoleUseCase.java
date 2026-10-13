package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.exceptions.InvalidUserRoleException;
import com.smxplore.proto.domain.exceptions.UserNotFoundException;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.AssignUserRoleUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class AssignUserRoleUseCase implements AssignUserRoleUseCasePort {

    private final UserRepositoryPort userRepository;

    @Override
    public Mono<User> handle(UUID userId, UserRole newRole) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .flatMap(user -> {
                    if (user.getRole() != newRole) {
                        return Mono.error(new InvalidUserRoleException(
                                "Changing a user's role is not supported: each role has its own profile data. "
                                        + "Create a new account with role '" + newRole + "' instead."));
                    }
                    return Mono.just(user);
                });
    }
}
