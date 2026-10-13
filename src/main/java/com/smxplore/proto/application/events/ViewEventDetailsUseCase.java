package com.smxplore.proto.application.events;

import com.smxplore.proto.domain.exceptions.event.EventNotFoundException;
import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.domain.ports.repository.EventRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.event.ViewEventDetailsUseCasePort;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class ViewEventDetailsUseCase implements ViewEventDetailsUseCasePort {

    private final EventRepositoryPort eventRepo;

    @Override
    public Mono<Event> handle(UUID eventId) {
        return eventRepo.findById(eventId)
                .switchIfEmpty(Mono.error(new EventNotFoundException(eventId)));
    }
}
