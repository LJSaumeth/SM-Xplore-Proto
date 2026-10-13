package com.smxplore.proto.domain.ports.usecases.activity;

import com.smxplore.proto.domain.model.activity.Activity;

import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface ManageAvailabilityUseCasePort {
    Mono<Activity> handle(UUID activityId, Instant startsAt, Instant endsAt);
}
