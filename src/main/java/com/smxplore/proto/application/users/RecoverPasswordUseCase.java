package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.RecoverPasswordUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RecoverPasswordUseCase implements RecoverPasswordUseCasePort {

    private final UserRepositoryPort userRepository;

    @Override
    public Mono<Void> handle(String email) {
        return userRepository.findByEmail(email).then();
    }
}
