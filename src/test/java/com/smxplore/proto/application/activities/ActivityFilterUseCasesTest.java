package com.smxplore.proto.application.activities;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.smxplore.proto.domain.model.activity.Activity;
import com.smxplore.proto.domain.model.site.Site;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ISiteRepository;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

class ActivityFilterUseCasesTest {

    private final ActivityRepositoryPort activityRepository = mock(ActivityRepositoryPort.class);
    private final ISiteRepository siteRepository = mock(ISiteRepository.class);

    private final FindActivitiesByZoneUseCase findActivitiesByZoneUseCase =
            new FindActivitiesByZoneUseCase(activityRepository, siteRepository);

    private Activity activity(UUID siteId) {
        Instant startsAt = Instant.parse("2026-11-01T10:00:00Z");
        return Activity.builder()
                .id(UUID.randomUUID())
                .siteId(siteId)
                .name("City Walking Tour")
                .category("Tour")
                .price(new BigDecimal("80000.00"))
                .maxCapacity(20)
                .durationMinutes(90)
                .startsAt(startsAt)
                .endsAt(startsAt.plusSeconds(5400))
                .build();
    }

    @Test
    void shouldListActivitiesOfTheSitesOfAZone() {
        var zoneId = UUID.randomUUID();
        var siteId = UUID.randomUUID();
        when(siteRepository.findAllByZoneId(zoneId))
                .thenReturn(Flux.just(new Site(siteId, zoneId, "Tayrona", 11.31, -74.06, List.of())));
        when(activityRepository.findAllBySiteIdIn(List.of(siteId))).thenReturn(Flux.just(activity(siteId)));

        StepVerifier.create(findActivitiesByZoneUseCase.handle(zoneId))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenTheZoneHasNoSites() {
        var zoneId = UUID.randomUUID();
        when(siteRepository.findAllByZoneId(zoneId)).thenReturn(Flux.empty());

        StepVerifier.create(findActivitiesByZoneUseCase.handle(zoneId))
                .verifyComplete();
    }
}
