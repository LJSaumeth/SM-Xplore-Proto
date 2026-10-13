package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.destinationmanager.DestinationManager;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ApproveDestinationManagerRegistrationUseCasePort {

    Mono<DestinationManager> handle(UUID destinationManagerId);
}
