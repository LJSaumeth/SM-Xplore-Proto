package com.smxplore.proto.application.activities;

import com.smxplore.proto.domain.model.activity.Activity;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.activity.FindActivitiesBySiteUseCasePort;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class FindActivitiesBySiteUseCase implements FindActivitiesBySiteUseCasePort {

    private final ActivityRepositoryPort activityRepo;

    @Override
    public Flux<Activity> handle(UUID siteId) {
        return activityRepo.findAllBySiteId(siteId);
    }
}
