package com.smxplore.proto;

import com.smxplore.proto.domain.model.activity.Activity;
import com.smxplore.proto.domain.model.activity.ActivityStatus;
import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.domain.model.event.EventStatus;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.repository.EventRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PostgresAdaptersIntegrationTest {

    @Autowired
    private EventRepositoryPort eventRepository;

    @Autowired
    private ActivityRepositoryPort activityRepository;

    @Test
    void eventRoundTripKeepsFieldsAndSupportsSearchAndUpdates() {
        var siteId = UUID.randomUUID();
        var startsAt = Instant.now().plusSeconds(3600).truncatedTo(ChronoUnit.MILLIS);
        var endsAt = startsAt.plusSeconds(7200);
        var event = Event.builder()
                .siteId(siteId)
                .name("Sunset Kayak Tour")
                .category("Nautical")
                .price(new BigDecimal("150000.50"))
                .maxCapacity(12)
                .startsAt(startsAt)
                .endsAt(endsAt)
                .description("Guided kayak tour at sunset.")
                .build();

        var saved = eventRepository.save(event).block();
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();

        var found = eventRepository.findById(saved.getId()).block();
        assertThat(found).isNotNull();
        assertThat(found.getSiteId()).isEqualTo(siteId);
        assertThat(found.getName()).isEqualTo("Sunset Kayak Tour");
        assertThat(found.getCategory()).isEqualTo("Nautical");
        assertThat(found.getStatus()).isEqualTo(EventStatus.DRAFT);
        assertThat(found.getPrice()).isEqualByComparingTo(new BigDecimal("150000.50"));
        assertThat(found.getMaxCapacity()).isEqualTo(12);
        assertThat(found.getStartsAt()).isEqualTo(startsAt);
        assertThat(found.getEndsAt()).isEqualTo(endsAt);
        assertThat(found.getDescription()).isEqualTo("Guided kayak tour at sunset.");
        assertThat(found.getExpectedAttendance()).isZero();

        var matches = eventRepository.searchByName("kayak").collectList().block();
        assertThat(matches).extracting(Event::getId).contains(saved.getId());

        var all = eventRepository.findAll().collectList().block();
        assertThat(all).extracting(Event::getId).contains(saved.getId());

        assertThat(eventRepository.existsById(saved.getId()).block()).isTrue();

        found.estimateAttendance(200);
        var updated = eventRepository.save(found).block();
        assertThat(updated).isNotNull();
        StepVerifier.create(eventRepository.findById(saved.getId()))
                .assertNext(reloaded -> assertThat(reloaded.getExpectedAttendance()).isEqualTo(200))
                .verifyComplete();

        StepVerifier.create(eventRepository.findById(UUID.randomUUID()))
                .verifyComplete();
    }

    @Test
    void activityRoundTripKeepsOptionalGuideAndSupportsFilters() {
        var siteId = UUID.randomUUID();
        var guideId = UUID.randomUUID();
        var startsAt = Instant.now().plusSeconds(3600).truncatedTo(ChronoUnit.MILLIS);
        var activity = Activity.builder()
                .siteId(siteId)
                .guideId(guideId)
                .name("City Walking Tour")
                .category("Tour")
                .price(new BigDecimal("80000.00"))
                .maxCapacity(20)
                .durationMinutes(90)
                .startsAt(startsAt)
                .endsAt(startsAt.plusSeconds(5400))
                .description("Historic downtown walk.")
                .build();

        var saved = activityRepository.save(activity).block();
        assertThat(saved).isNotNull();

        var found = activityRepository.findById(saved.getId()).block();
        assertThat(found).isNotNull();
        assertThat(found.getGuideId()).isEqualTo(guideId);
        assertThat(found.getCategory()).isEqualTo("Tour");
        assertThat(found.getStatus()).isEqualTo(ActivityStatus.DRAFT);
        assertThat(found.getDurationMinutes()).isEqualTo(90);

        var byCategory = activityRepository.findAllByCategory("tour").collectList().block();
        assertThat(byCategory).extracting(Activity::getId).contains(saved.getId());

        var bySite = activityRepository.findAllBySiteId(siteId).collectList().block();
        assertThat(bySite).extracting(Activity::getId).contains(saved.getId());

        // The update path (details changed and persisted through save) keeps the same row
        found.updateDetails("City Walking Tour Deluxe", "Tour", "Updated description.", new BigDecimal("95000.00"));
        var updated = activityRepository.save(found).block();
        assertThat(updated).isNotNull();
        StepVerifier.create(activityRepository.findById(saved.getId()))
                .assertNext(reloaded -> {
                    assertThat(reloaded.getName()).isEqualTo("City Walking Tour Deluxe");
                    assertThat(reloaded.getPrice()).isEqualByComparingTo(new BigDecimal("95000.00"));
                })
                .verifyComplete();

        // An activity may not have a guide (optional)
        var withoutGuide = Activity.builder()
                .siteId(siteId)
                .name("Free Museum Day")
                .category("Culture")
                .price(BigDecimal.ZERO)
                .maxCapacity(50)
                .durationMinutes(120)
                .startsAt(startsAt)
                .endsAt(startsAt.plusSeconds(7200))
                .description("Open doors day.")
                .build();
        var savedWithoutGuide = activityRepository.save(withoutGuide).block();
        assertThat(savedWithoutGuide).isNotNull();
        StepVerifier.create(activityRepository.findById(savedWithoutGuide.getId()))
                .assertNext(reloaded -> assertThat(reloaded.getGuideId()).isNull())
                .verifyComplete();
    }
}
