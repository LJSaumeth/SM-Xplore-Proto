package com.smxplore.proto.application.events;

import com.smxplore.proto.domain.exceptions.event.EventNotFoundException;
import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.domain.ports.repository.EventRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.event.EstimateEventAttendanceUseCasePort;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class EstimateEventAttendanceUseCase implements EstimateEventAttendanceUseCasePort {

    private final EventRepositoryPort eventRepo;

    @Override
    public Mono<Event> handle(UUID eventId, int expectedAttendance) {
        return eventRepo.findById(eventId)
                .switchIfEmpty(Mono.error(new EventNotFoundException(eventId)))
                .map(event -> estimate(event, expectedAttendance))
                .flatMap(eventRepo::save);
    }

    private Event estimate(Event event, int expectedAttendance) {
        event.estimateAttendance(expectedAttendance);
        return event;
    }
}
