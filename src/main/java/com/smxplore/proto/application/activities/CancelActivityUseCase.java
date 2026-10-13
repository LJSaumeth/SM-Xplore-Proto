package com.smxplore.proto.application.activities;

import com.smxplore.proto.domain.exceptions.activity.ActivityNotFoundException;
import com.smxplore.proto.domain.model.activity.Activity;
import com.smxplore.proto.domain.model.activity.ActivityStatus;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.activity.CancelActivityUseCasePort;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class CancelActivityUseCase implements CancelActivityUseCasePort {

    private final ActivityRepositoryPort activityRepo;

    @Override
    public Mono<Activity> handle(UUID activityId) {
        return activityRepo.findById(activityId)
                .switchIfEmpty(Mono.error(new ActivityNotFoundException(activityId)))
                .map(this::cancel)
                .flatMap(activityRepo::save);
    }

    private Activity cancel(Activity activity) {
        activity.changeStatus(ActivityStatus.CANCELLED);
        return activity;
    }
}
