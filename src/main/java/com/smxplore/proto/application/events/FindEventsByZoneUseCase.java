package com.smxplore.proto.application.events;

import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.domain.model.site.Site;
import com.smxplore.proto.domain.ports.repository.EventRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ISiteRepository;
import com.smxplore.proto.domain.ports.usecases.event.FindEventsByZoneUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class FindEventsByZoneUseCase implements FindEventsByZoneUseCasePort {

    private final EventRepositoryPort eventRepository;
    private final ISiteRepository siteRepository;

    @Override
    public Flux<Event> handle(UUID zoneId) {
        return siteRepository.findAllByZoneId(zoneId)
                .map(Site::getId)
                .collectList()
                .flatMapMany(siteIds -> siteIds.isEmpty()
                        ? Flux.empty()
                        : eventRepository.findAllBySiteIdIn(siteIds));
    }
}
