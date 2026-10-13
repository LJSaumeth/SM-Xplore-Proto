package com.smxplore.proto.application.events;

import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.domain.ports.repository.EventRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.event.FindEventsBySiteUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class FindEventsBySiteUseCase implements FindEventsBySiteUseCasePort {

    private final EventRepositoryPort eventRepository;

    @Override
    public Flux<Event> handle(UUID siteId) {
        return eventRepository.findAllBySiteId(siteId);
    }
}
