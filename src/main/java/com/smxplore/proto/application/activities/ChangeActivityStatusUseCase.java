package com.smxplore.proto.application.activities;

import com.smxplore.proto.domain.exceptions.activity.ActivityNotFoundException;
import com.smxplore.proto.domain.model.activity.Activity;
import com.smxplore.proto.domain.model.activity.ActivityStatus;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.activity.ChangeActivityStatusUseCasePort;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class ChangeActivityStatusUseCase implements ChangeActivityStatusUseCasePort {

    private final ActivityRepositoryPort activityRepo;

    @Override
    public Mono<Activity> handle(UUID activityId, ActivityStatus newStatus) {
        return activityRepo.existsById(activityId)
                .filter(exists -> exists)
                .switchIfEmpty(Mono.error(new ActivityNotFoundException(activityId)))
                .flatMap(exists -> activityRepo.findById(activityId))
                .switchIfEmpty(Mono.error(new ActivityNotFoundException(activityId)))
                .map(activity -> changeStatus(activity, newStatus))
                .flatMap(activityRepo::save);
    }

    private Activity changeStatus(Activity activity, ActivityStatus newStatus) {
        activity.changeStatus(newStatus);
        return activity;
    }
}
