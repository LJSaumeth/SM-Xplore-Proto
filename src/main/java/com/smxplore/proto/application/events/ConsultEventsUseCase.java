package com.smxplore.proto.application.events;

import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.domain.ports.repository.EventRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.event.ConsultEventsUseCasePort;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ConsultEventsUseCase implements ConsultEventsUseCasePort {

    private final EventRepositoryPort eventRepo;

    @Override
    public Flux<Event> handle() {
        return eventRepo.findAll();
    }
}
