package com.smxplore.proto.application.events;

import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.domain.ports.repository.EventRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.event.SearchEventsUseCasePort;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class SearchEventsUseCase implements SearchEventsUseCasePort {

    private final EventRepositoryPort eventRepo;

    @Override
    public Flux<Event> handle(String name) {
        return eventRepo.searchByName(name);
    }
}
