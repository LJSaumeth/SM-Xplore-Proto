package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.exceptions.UserNotFoundException;
import com.smxplore.proto.domain.model.destinationmanager.DestinationManager;
import com.smxplore.proto.domain.ports.repository.DestinationManagerRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.ApproveDestinationManagerRegistrationUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class ApproveDestinationManagerRegistrationUseCase implements ApproveDestinationManagerRegistrationUseCasePort {

    private final DestinationManagerRepositoryPort destinationManagerRepository;

    @Override
    public Mono<DestinationManager> handle(UUID destinationManagerId) {
        return destinationManagerRepository.findById(destinationManagerId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(destinationManagerId)))
                .doOnNext(DestinationManager::approveRegistration)
                .flatMap(destinationManagerRepository::save);
    }
}
