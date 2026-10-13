package com.smxplore.proto.application.activities;

import com.smxplore.proto.domain.exceptions.activity.ActivityNotFoundException;
import com.smxplore.proto.domain.model.activity.Activity;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.activity.ManageAvailabilityUseCasePort;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class ManageAvailabilityUseCase implements ManageAvailabilityUseCasePort {

    private final ActivityRepositoryPort activityRepo;

    @Override
    public Mono<Activity> handle(UUID activityId, Instant startsAt, Instant endsAt) {
        return activityRepo.findById(activityId)
                .switchIfEmpty(Mono.error(new ActivityNotFoundException(activityId)))
                .map(activity -> updateSchedule(activity, startsAt, endsAt))
                .flatMap(activityRepo::save);
    }

    private Activity updateSchedule(Activity activity, Instant startsAt, Instant endsAt) {
        activity.updateSchedule(startsAt, endsAt);
        return activity;
    }
}
