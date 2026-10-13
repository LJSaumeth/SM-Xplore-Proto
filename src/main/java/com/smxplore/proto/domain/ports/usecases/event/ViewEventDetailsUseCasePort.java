package com.smxplore.proto.domain.ports.usecases.event;

import com.smxplore.proto.domain.model.event.Event;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ViewEventDetailsUseCasePort {
    Mono<Event> handle(UUID eventId);
}
