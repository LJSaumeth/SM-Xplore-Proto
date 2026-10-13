package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.exceptions.InvalidUserDataException;
import com.smxplore.proto.domain.exceptions.UserNotFoundException;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.BlockOrSuspendUserUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class BlockOrSuspendUserUseCase implements BlockOrSuspendUserUseCasePort {

    private static final String INVALID_TARGET_MESSAGE = "Target status must be ACTIVE, BLOCKED or SUSPENDED.";

    private final UserRepositoryPort userRepository;

    @Override
    public Mono<User> handle(UUID userId, UserStatus targetStatus) {
        return Mono.justOrEmpty(targetStatus)
                .filter(BlockOrSuspendUserUseCase::isChangeable)
                .switchIfEmpty(Mono.error(new InvalidUserDataException(INVALID_TARGET_MESSAGE)))
                .flatMap(status -> userRepository.findById(userId)
                        .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                        .doOnNext(user -> applyStatus(user, status))
                        .flatMap(userRepository::save));
    }

    private static boolean isChangeable(UserStatus status) {
        return status == UserStatus.ACTIVE || status == UserStatus.BLOCKED || status == UserStatus.SUSPENDED;
    }

    private static void applyStatus(User user, UserStatus status) {
        switch (status) {
            case ACTIVE -> {
                if (user.getStatus() == UserStatus.INACTIVE) {
                    throw new InvalidUserDataException(
                            "Inactive accounts cannot be activated directly; pending registrations must be approved.");
                }
                user.activate();
            }
            case BLOCKED -> user.block();
            case SUSPENDED -> user.suspend();
            default -> throw new InvalidUserDataException(INVALID_TARGET_MESSAGE);
        }
    }
}
