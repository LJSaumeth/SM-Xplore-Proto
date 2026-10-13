package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.event.Event;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

public interface EventRepositoryPort {

    Mono<Event> save(Event event);

    Mono<Event> findById(UUID id);

    Mono<Boolean> existsById(UUID id);

    Flux<Event> findAll();

    Flux<Event> searchByName(String name);

    Flux<Event> findAllBySiteId(UUID siteId);

    Flux<Event> findAllBySiteIdIn(Collection<UUID> siteIds);
}
