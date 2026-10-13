package com.smxplore.proto.application.activities;

import com.smxplore.proto.domain.model.activity.Activity;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.activity.ConsultActivitiesUseCasePort;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ConsultActivitiesUseCase implements ConsultActivitiesUseCasePort {

    private final ActivityRepositoryPort activityRepo;

    @Override
    public Flux<Activity> handle() {
        return activityRepo.findAll();
    }
}
