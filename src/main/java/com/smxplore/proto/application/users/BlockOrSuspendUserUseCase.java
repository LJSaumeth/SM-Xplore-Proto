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

    private final UserRepositoryPort userRepository;

    @Override
    public Mono<User> handle(UUID userId, UserStatus targetStatus) {
        if (targetStatus != UserStatus.BLOCKED && targetStatus != UserStatus.SUSPENDED) {
            return Mono.error(new InvalidUserDataException("Target status must be BLOCKED or SUSPENDED."));
        }
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .doOnNext(user -> {
                    if (targetStatus == UserStatus.BLOCKED) {
                        user.block();
                    } else {
                        user.suspend();
                    }
                })
                .flatMap(userRepository::save);
    }
}
