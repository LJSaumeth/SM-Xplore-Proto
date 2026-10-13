package com.smxplore.proto.application.events;

import com.smxplore.proto.domain.exceptions.event.EventNotFoundException;
import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.domain.model.event.EventStatus;
import com.smxplore.proto.domain.ports.repository.EventRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.event.ChangeEventStatusUseCasePort;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class ChangeEventStatusUseCase implements ChangeEventStatusUseCasePort {

    private final EventRepositoryPort eventRepo;

    @Override
    public Mono<Event> handle(UUID eventId, EventStatus newStatus) {
        return eventRepo.existsById(eventId)
                .filter(exists -> exists)
                .switchIfEmpty(Mono.error(new EventNotFoundException(eventId)))
                .flatMap(exists -> eventRepo.findById(eventId))
                .switchIfEmpty(Mono.error(new EventNotFoundException(eventId)))
                .map(event -> changeStatus(event, newStatus))
                .flatMap(eventRepo::save);
    }

    private Event changeStatus(Event event, EventStatus newStatus) {
        event.changeStatus(newStatus);
        return event;
    }
}
