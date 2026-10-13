package com.smxplore.proto.application.events;

import com.smxplore.proto.domain.exceptions.event.EventNotFoundException;
import com.smxplore.proto.domain.exceptions.event.InvalidEventRequestException;
import com.smxplore.proto.domain.exceptions.event.InvalidEventStatusTransitionException;
import com.smxplore.proto.domain.exceptions.event.InvalidExpectedAttendanceException;
import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.domain.model.event.EventStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventUseCasesTest {

    private FakeEventRepository eventRepo;
    private RegisterEventUseCase registerEvent;
    private ChangeEventStatusUseCase changeEventStatus;
    private EstimateEventAttendanceUseCase estimateEventAttendance;
    private ConsultEventsUseCase consultEvents;
    private SearchEventsUseCase searchEvents;
    private ViewEventDetailsUseCase viewEventDetails;
    private UpdateEventUseCase updateEvent;

    @BeforeEach
    void setUp() {
        eventRepo = new FakeEventRepository();
        registerEvent = new RegisterEventUseCase(eventRepo);
        changeEventStatus = new ChangeEventStatusUseCase(eventRepo);
        estimateEventAttendance = new EstimateEventAttendanceUseCase(eventRepo);
        consultEvents = new ConsultEventsUseCase(eventRepo);
        searchEvents = new SearchEventsUseCase(eventRepo);
        viewEventDetails = new ViewEventDetailsUseCase(eventRepo);
        updateEvent = new UpdateEventUseCase(eventRepo);
    }

    private Event eventWith(UUID id, String name) {
        Instant startsAt = Instant.now().plusSeconds(3600);
        return Event.builder()
                .id(id)
                .siteId(UUID.randomUUID())
                .name(name)
                .category("Nautical")
                .price(new BigDecimal("150000.00"))
                .maxCapacity(12)
                .startsAt(startsAt)
                .endsAt(startsAt.plusSeconds(7200))
                .description("Guided kayak tour at sunset.")
                .build();
    }

    private Event eventWith(UUID id) {
        return eventWith(id, "Sunset Kayak Tour");
    }

    @Test
    void shouldRegisterValidEvent() {
        UUID id = UUID.randomUUID();

        Event saved = Objects.requireNonNull(registerEvent.handle(eventWith(id)).block());

        assertEquals(id, saved.getId());
        assertTrue(Objects.requireNonNull(eventRepo.existsById(id).block()));
    }

    @Test
    void shouldFailRegisterWhenEventIsNull() {
        assertThrows(InvalidEventRequestException.class, () -> registerEvent.handle(null).block());
    }

    @Test
    void shouldPropagateValidationErrorOnRegister() {
        Event invalid = eventWith(UUID.randomUUID()).toBuilder().siteId(null).build();

        assertThrows(InvalidEventRequestException.class, () -> registerEvent.handle(invalid).block());
    }

    @Test
    void shouldChangeStatusOfExistingEvent() {
        UUID id = UUID.randomUUID();
        eventRepo.save(eventWith(id)).block();

        Event updated = Objects.requireNonNull(changeEventStatus.handle(id, EventStatus.PUBLISHED).block());

        assertEquals(EventStatus.PUBLISHED, updated.getStatus());
    }

    @Test
    void shouldFailChangingStatusWhenEventDoesNotExist() {
        assertThrows(EventNotFoundException.class,
                () -> changeEventStatus.handle(UUID.randomUUID(), EventStatus.PUBLISHED).block());
    }

    @Test
    void shouldFailChangingStatusWithInvalidTransition() {
        UUID id = UUID.randomUUID();
        eventRepo.save(eventWith(id).toBuilder().status(EventStatus.CANCELLED).build()).block();

        assertThrows(InvalidEventStatusTransitionException.class,
                () -> changeEventStatus.handle(id, EventStatus.PUBLISHED).block());
    }

    @Test
    void shouldEstimateAttendance() {
        UUID id = UUID.randomUUID();
        eventRepo.save(eventWith(id)).block();

        Event updated = Objects.requireNonNull(estimateEventAttendance.handle(id, 200).block());

        assertEquals(200, updated.getExpectedAttendance());
    }

    @Test
    void shouldFailEstimateWhenEventDoesNotExist() {
        assertThrows(EventNotFoundException.class,
                () -> estimateEventAttendance.handle(UUID.randomUUID(), 100).block());
    }

    @Test
    void shouldFailEstimateWithNegativeAttendance() {
        UUID id = UUID.randomUUID();
        eventRepo.save(eventWith(id)).block();

        assertThrows(InvalidExpectedAttendanceException.class,
                () -> estimateEventAttendance.handle(id, -1).block());
    }

    @Test
    void shouldConsultAllEvents() {
        eventRepo.save(eventWith(UUID.randomUUID(), "Sunset Kayak Tour")).block();
        eventRepo.save(eventWith(UUID.randomUUID(), "City Walking Tour")).block();

        List<Event> events = Objects.requireNonNull(consultEvents.handle().collectList().block());

        assertEquals(2, events.size());
    }

    @Test
    void shouldSearchEventsByName() {
        eventRepo.save(eventWith(UUID.randomUUID(), "Sunset Kayak Tour")).block();
        eventRepo.save(eventWith(UUID.randomUUID(), "City Walking Tour")).block();

        List<Event> matches = Objects.requireNonNull(searchEvents.handle("kayak").collectList().block());

        assertEquals(1, matches.size());
        assertEquals("Sunset Kayak Tour", matches.getFirst().getName());
    }

    @Test
    void shouldViewEventDetails() {
        UUID id = UUID.randomUUID();
        eventRepo.save(eventWith(id)).block();

        Event found = Objects.requireNonNull(viewEventDetails.handle(id).block());

        assertEquals(id, found.getId());
    }

    @Test
    void shouldFailViewDetailsWhenEventDoesNotExist() {
        assertThrows(EventNotFoundException.class,
                () -> viewEventDetails.handle(UUID.randomUUID()).block());
    }

    @Test
    void shouldUpdateEventDetails() {
        UUID id = UUID.randomUUID();
        eventRepo.save(eventWith(id)).block();
        Instant newStart = Instant.now().plusSeconds(7200);

        Event updated = Objects.requireNonNull(updateEvent.handle(id, "Updated Tour", "Culture",
                new BigDecimal("99000.00"), 20, newStart, newStart.plusSeconds(3600), "Updated").block());

        assertEquals("Updated Tour", updated.getName());
        assertEquals("Culture", updated.getCategory());
        assertEquals(20, updated.getMaxCapacity());
    }

    @Test
    void shouldFailUpdateWhenEventDoesNotExist() {
        Instant startsAt = Instant.now().plusSeconds(3600);

        assertThrows(EventNotFoundException.class, () -> updateEvent.handle(UUID.randomUUID(),
                "Updated Tour", "Culture", new BigDecimal("99000.00"), 20, startsAt,
                startsAt.plusSeconds(3600), "Updated").block());
    }
}
