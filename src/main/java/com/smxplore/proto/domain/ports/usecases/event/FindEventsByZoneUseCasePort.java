package com.smxplore.proto.domain.ports.usecases.event;

import com.smxplore.proto.domain.model.event.Event;

import reactor.core.publisher.Flux;

import java.util.UUID;

public interface FindEventsByZoneUseCasePort {

    Flux<Event> handle(UUID zoneId);
}
