package com.smxplore.proto.domain.ports.usecases.event;

import com.smxplore.proto.domain.model.event.Event;

import reactor.core.publisher.Flux;

public interface ConsultEventsUseCasePort {
    Flux<Event> handle();
}
