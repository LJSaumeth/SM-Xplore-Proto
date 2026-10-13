package com.smxplore.proto.domain.ports.usecases.event;

import com.smxplore.proto.domain.model.event.Event;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface UpdateEventUseCasePort {

    Mono<Event> handle(UUID eventId, String name, String category, BigDecimal price, int maxCapacity,
                       Instant startsAt, Instant endsAt, String description);
}
