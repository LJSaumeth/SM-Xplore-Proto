package com.smxplore.proto.infrastructure.adapters.in.web.controller;

import com.smxplore.proto.domain.exceptions.activity.ActivityNotFoundException;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.activity.CancelActivityUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.ChangeActivityStatusUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.ConsultActivitiesUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.FindActivitiesByCategoryUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.FindActivitiesBySiteUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.FindActivitiesByZoneUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.ManageAvailabilityUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.ManageSlotsUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.RegisterActivityUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.UpdateActivityUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.ViewActivityDetailsUseCasePort;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ActivityRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ActivityResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.AvailabilityRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ChangeActivityStatusRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.SlotsRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.UpdateActivityRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.ActivityWebMapper;
import com.smxplore.proto.infrastructure.config.JwtConfiguration;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
@Tag(name = "Activities", description = "Tourist activities offered at the sites")
public class ActivityController {

    private final RegisterActivityUseCasePort registerActivityUseCase;
    private final ViewActivityDetailsUseCasePort viewActivityDetailsUseCase;
    private final ConsultActivitiesUseCasePort consultActivitiesUseCase;
    private final FindActivitiesByCategoryUseCasePort findActivitiesByCategoryUseCase;
    private final FindActivitiesBySiteUseCasePort findActivitiesBySiteUseCase;
    private final FindActivitiesByZoneUseCasePort findActivitiesByZoneUseCase;
    private final UpdateActivityUseCasePort updateActivityUseCase;
    private final ManageAvailabilityUseCasePort manageAvailabilityUseCase;
    private final ManageSlotsUseCasePort manageSlotsUseCase;
    private final CancelActivityUseCasePort cancelActivityUseCase;
    private final ChangeActivityStatusUseCasePort changeActivityStatusUseCase;
    private final ActivityRepositoryPort activityRepository;
    private final ActivityWebMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register an activity for a site (guides register their own)")
    public Mono<ActivityResponse> register(@Valid @RequestBody ActivityRequest request,
                                           @AuthenticationPrincipal Jwt jwt) {
        var activity = mapper.toDomain(request);
        if (isGuide(jwt)) {
            activity = activity.toBuilder().guideId(UUID.fromString(jwt.getSubject())).build();
        }
        return registerActivityUseCase.handle(activity)
                .map(mapper::toResponse);
    }

    @GetMapping("/{activityId}")
    @Operation(summary = "Get one activity by id")
    public Mono<ActivityResponse> findOne(@PathVariable UUID activityId) {
        return viewActivityDetailsUseCase.handle(activityId).map(mapper::toResponse);
    }

    @GetMapping
    @Operation(summary = "List all activities")
    public Flux<ActivityResponse> findAll() {
        return consultActivitiesUseCase.handle().map(mapper::toResponse);
    }

    @GetMapping("/search")
    @Operation(summary = "List activities by category")
    public Flux<ActivityResponse> findByCategory(@RequestParam String category) {
        return findActivitiesByCategoryUseCase.handle(category).map(mapper::toResponse);
    }

    @GetMapping("/site/{siteId}")
    @Operation(summary = "List the activities of a site")
    public Flux<ActivityResponse> findBySite(@PathVariable UUID siteId) {
        return findActivitiesBySiteUseCase.handle(siteId).map(mapper::toResponse);
    }

    @GetMapping("/zone/{zoneId}")
    @Operation(summary = "List the activities of the sites of a zone")
    public Flux<ActivityResponse> findByZone(@PathVariable UUID zoneId) {
        return findActivitiesByZoneUseCase.handle(zoneId).map(mapper::toResponse);
    }

    @PatchMapping("/{activityId}")
    @Operation(summary = "Update the details of an activity (its guide)")
    public Mono<ActivityResponse> update(@PathVariable UUID activityId,
                                         @Valid @RequestBody UpdateActivityRequest request,
                                         @AuthenticationPrincipal Jwt jwt) {
        return requireActivityAccess(jwt, activityId)
                .then(Mono.defer(() -> updateActivityUseCase.handle(activityId, request.name(), request.category(),
                        request.description(), request.price())))
                .map(mapper::toResponse);
    }

    @PatchMapping("/{activityId}/availability")
    @Operation(summary = "Change the availability window of an activity (its guide)")
    public Mono<ActivityResponse> manageAvailability(@PathVariable UUID activityId,
                                                     @Valid @RequestBody AvailabilityRequest request,
                                                     @AuthenticationPrincipal Jwt jwt) {
        return requireActivityAccess(jwt, activityId)
                .then(Mono.defer(() -> manageAvailabilityUseCase.handle(activityId, request.startsAt(),
                        request.endsAt())))
                .map(mapper::toResponse);
    }

    @PatchMapping("/{activityId}/slots")
    @Operation(summary = "Change the number of slots of an activity (its guide)")
    public Mono<ActivityResponse> manageSlots(@PathVariable UUID activityId,
                                              @Valid @RequestBody SlotsRequest request,
                                              @AuthenticationPrincipal Jwt jwt) {
        return requireActivityAccess(jwt, activityId)
                .then(Mono.defer(() -> manageSlotsUseCase.handle(activityId, request.maxCapacity())))
                .map(mapper::toResponse);
    }

    @PostMapping("/{activityId}/cancel")
    @Operation(summary = "Cancel an activity (its guide)")
    public Mono<ActivityResponse> cancel(@PathVariable UUID activityId, @AuthenticationPrincipal Jwt jwt) {
        return requireActivityAccess(jwt, activityId)
                .then(Mono.defer(() -> cancelActivityUseCase.handle(activityId)))
                .map(mapper::toResponse);
    }

    @PatchMapping("/{activityId}/status")
    @Operation(summary = "Change the status of an activity (its guide)")
    public Mono<ActivityResponse> changeStatus(@PathVariable UUID activityId,
                                               @Valid @RequestBody ChangeActivityStatusRequest request,
                                               @AuthenticationPrincipal Jwt jwt) {
        return requireActivityAccess(jwt, activityId)
                .then(Mono.defer(() -> changeActivityStatusUseCase.handle(activityId, request.status())))
                .map(mapper::toResponse);
    }

    private static boolean isGuide(Jwt jwt) {
        return UserRole.GUIDE.name().equals(jwt.getClaimAsString(JwtConfiguration.ROLE_CLAIM));
    }

    private static boolean isAdminOrManager(Jwt jwt) {
        var role = jwt.getClaimAsString(JwtConfiguration.ROLE_CLAIM);
        return UserRole.ADMIN.name().equals(role) || UserRole.DESTINATION_MANAGER.name().equals(role);
    }

    private Mono<Void> requireActivityAccess(Jwt jwt, UUID activityId) {
        if (isAdminOrManager(jwt)) {
            return Mono.empty();
        }
        return activityRepository.findById(activityId)
                .switchIfEmpty(Mono.error(new ActivityNotFoundException(activityId)))
                .doOnNext(activity -> {
                    if (activity.getGuideId() == null
                            || !activity.getGuideId().toString().equals(jwt.getSubject())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                "Guides can only manage their own activities.");
                    }
                })
                .then();
    }
}
