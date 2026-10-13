package com.smxplore.proto.infrastructure.adapters.in.web.controller;

import com.smxplore.proto.domain.exceptions.event.EventNotFoundException;
import com.smxplore.proto.domain.model.event.EventStatus;
import com.smxplore.proto.domain.ports.usecases.event.ChangeEventStatusUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.ConsultEventsUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.EstimateEventAttendanceUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.FindEventsBySiteUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.FindEventsByZoneUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.RegisterEventUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.SearchEventsUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.UpdateEventUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.ViewEventDetailsUseCasePort;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ChangeEventStatusRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.EstimateAttendanceRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.EventRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.EventResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.UpdateEventRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.EventWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Events hosted at the tourist sites")
public class EventController {

    private final RegisterEventUseCasePort registerEventUseCase;
    private final ViewEventDetailsUseCasePort viewEventDetailsUseCase;
    private final ConsultEventsUseCasePort consultEventsUseCase;
    private final SearchEventsUseCasePort searchEventsUseCase;
    private final ChangeEventStatusUseCasePort changeEventStatusUseCase;
    private final EstimateEventAttendanceUseCasePort estimateEventAttendanceUseCase;
    private final FindEventsBySiteUseCasePort findEventsBySiteUseCase;
    private final FindEventsByZoneUseCasePort findEventsByZoneUseCase;
    private final UpdateEventUseCasePort updateEventUseCase;
    private final EventWebMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register an event for a site")
    public Mono<EventResponse> register(@Valid @RequestBody EventRequest request) {
        return registerEventUseCase.handle(mapper.toDomain(request))
                .map(mapper::toResponse);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Get one event by id (unpublished events are hidden from anonymous callers)")
    public Mono<EventResponse> findOne(@PathVariable UUID eventId, @AuthenticationPrincipal Jwt jwt) {
        return viewEventDetailsUseCase.handle(eventId)
                .map(mapper::toResponse)
                .filter(response -> jwt != null || isPublished(response))
                .switchIfEmpty(Mono.error(new EventNotFoundException(eventId)));
    }

    @GetMapping
    @Operation(summary = "List all events (anonymous callers only see published ones)")
    public Flux<EventResponse> findAll(@AuthenticationPrincipal Jwt jwt) {
        return visibleTo(jwt, consultEventsUseCase.handle().map(mapper::toResponse));
    }

    @GetMapping("/search")
    @Operation(summary = "Search events by name (anonymous callers only see published ones)")
    public Flux<EventResponse> search(@RequestParam String name, @AuthenticationPrincipal Jwt jwt) {
        return visibleTo(jwt, searchEventsUseCase.handle(name).map(mapper::toResponse));
    }

    @PatchMapping("/{eventId}/status")
    @Operation(summary = "Change the status of an event")
    public Mono<EventResponse> changeStatus(@PathVariable UUID eventId,
                                            @Valid @RequestBody ChangeEventStatusRequest request) {
        return changeEventStatusUseCase.handle(eventId, request.status()).map(mapper::toResponse);
    }

    @PatchMapping("/{eventId}/attendance")
    @Operation(summary = "Estimate the expected attendance of an event")
    public Mono<EventResponse> estimateAttendance(@PathVariable UUID eventId,
                                                  @Valid @RequestBody EstimateAttendanceRequest request) {
        return estimateEventAttendanceUseCase.handle(eventId, request.expectedAttendance())
                .map(mapper::toResponse);
    }

    @GetMapping("/site/{siteId}")
    @Operation(summary = "List the events of a site (anonymous callers only see published ones)")
    public Flux<EventResponse> findBySite(@PathVariable UUID siteId, @AuthenticationPrincipal Jwt jwt) {
        return visibleTo(jwt, findEventsBySiteUseCase.handle(siteId).map(mapper::toResponse));
    }

    @GetMapping("/zone/{zoneId}")
    @Operation(summary = "List the events of the sites of a zone (anonymous callers only see published ones)")
    public Flux<EventResponse> findByZone(@PathVariable UUID zoneId, @AuthenticationPrincipal Jwt jwt) {
        return visibleTo(jwt, findEventsByZoneUseCase.handle(zoneId).map(mapper::toResponse));
    }

    @PutMapping("/{eventId}")
    @Operation(summary = "Update the details of an event")
    public Mono<EventResponse> update(@PathVariable UUID eventId,
                                      @Valid @RequestBody UpdateEventRequest request) {
        return updateEventUseCase.handle(eventId, request.name(), request.category(), request.price(),
                        request.maxCapacity(), request.startsAt(), request.endsAt(), request.description())
                .map(mapper::toResponse);
    }

    private static Flux<EventResponse> visibleTo(Jwt jwt, Flux<EventResponse> events) {
        return jwt == null ? events.filter(EventController::isPublished) : events;
    }

    private static boolean isPublished(EventResponse response) {
        return response.status() == EventStatus.PUBLISHED;
    }
}
