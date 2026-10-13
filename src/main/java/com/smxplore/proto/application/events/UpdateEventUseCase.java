package com.smxplore.proto.application.events;

import com.smxplore.proto.domain.exceptions.event.EventNotFoundException;
import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.domain.ports.repository.EventRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.event.UpdateEventUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class UpdateEventUseCase implements UpdateEventUseCasePort {

    private final EventRepositoryPort eventRepository;

    @Override
    public Mono<Event> handle(UUID eventId, String name, String category, BigDecimal price, int maxCapacity,
                              Instant startsAt, Instant endsAt, String description) {
        return eventRepository.findById(eventId)
                .switchIfEmpty(Mono.error(new EventNotFoundException(eventId)))
                .doOnNext(event -> event.updateDetails(name, category, price, maxCapacity, startsAt, endsAt, description))
                .flatMap(eventRepository::save);
    }
}
