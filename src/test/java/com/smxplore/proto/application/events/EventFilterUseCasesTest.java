package com.smxplore.proto.application.events;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.domain.model.site.Site;
import com.smxplore.proto.domain.ports.repository.EventRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ISiteRepository;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

class EventFilterUseCasesTest {

    private final EventRepositoryPort eventRepository = mock(EventRepositoryPort.class);
    private final ISiteRepository siteRepository = mock(ISiteRepository.class);

    private final FindEventsBySiteUseCase findEventsBySiteUseCase = new FindEventsBySiteUseCase(eventRepository);
    private final FindEventsByZoneUseCase findEventsByZoneUseCase =
            new FindEventsByZoneUseCase(eventRepository, siteRepository);

    private Event event(UUID siteId) {
        Instant startsAt = Instant.parse("2026-11-01T10:00:00Z");
        return Event.builder()
                .id(UUID.randomUUID())
                .siteId(siteId)
                .name("Sunset Kayak Tour")
                .category("Nautical")
                .price(new BigDecimal("150000.00"))
                .maxCapacity(12)
                .startsAt(startsAt)
                .endsAt(startsAt.plusSeconds(7200))
                .build();
    }

    @Test
    void shouldListEventsOfASite() {
        var siteId = UUID.randomUUID();
        when(eventRepository.findAllBySiteId(siteId)).thenReturn(Flux.just(event(siteId)));

        StepVerifier.create(findEventsBySiteUseCase.handle(siteId))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldListEventsOfTheSitesOfAZone() {
        var zoneId = UUID.randomUUID();
        var siteId = UUID.randomUUID();
        when(siteRepository.findAllByZoneId(zoneId))
                .thenReturn(Flux.just(new Site(siteId, zoneId, "Tayrona", 11.31, -74.06, List.of())));
        when(eventRepository.findAllBySiteIdIn(List.of(siteId))).thenReturn(Flux.just(event(siteId)));

        StepVerifier.create(findEventsByZoneUseCase.handle(zoneId))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenTheZoneHasNoSites() {
        var zoneId = UUID.randomUUID();
        when(siteRepository.findAllByZoneId(zoneId)).thenReturn(Flux.empty());

        StepVerifier.create(findEventsByZoneUseCase.handle(zoneId))
                .verifyComplete();
    }
}
