package com.smxplore.proto.domain.ports.usecases.activity;

import com.smxplore.proto.domain.model.activity.Activity;

import reactor.core.publisher.Flux;

public interface FindActivitiesByCategoryUseCasePort {
    Flux<Activity> handle(String category);
}
