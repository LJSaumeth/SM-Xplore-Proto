package com.smxplore.proto.domain.ports.usecases.event;

import com.smxplore.proto.domain.model.event.Event;

import reactor.core.publisher.Mono;

public interface RegisterEventUseCasePort {
    Mono<Event> handle(Event event);
}
