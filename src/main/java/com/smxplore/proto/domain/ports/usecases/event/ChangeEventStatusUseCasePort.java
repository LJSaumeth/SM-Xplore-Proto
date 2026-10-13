package com.smxplore.proto.domain.ports.usecases.event;

import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.domain.model.event.EventStatus;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ChangeEventStatusUseCasePort {
    Mono<Event> handle(UUID eventId, EventStatus newStatus);
}
