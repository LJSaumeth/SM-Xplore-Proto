package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.domain.model.event.EventStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventRepositoryPort {

    Mono<Event> save(Event event);

    Mono<Event> findById(UUID id);

    Mono<Boolean> existsById(UUID id);

    Flux<Event> findAll();

    Flux<Event> findAllByStatus(EventStatus status);

    Flux<Event> findAllBySiteId(UUID siteId);

    Flux<Event> searchByName(String name);
}
