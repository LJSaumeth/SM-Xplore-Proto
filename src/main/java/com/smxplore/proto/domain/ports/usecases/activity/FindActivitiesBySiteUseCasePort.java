package com.smxplore.proto.domain.ports.usecases.activity;

import com.smxplore.proto.domain.model.activity.Activity;

import reactor.core.publisher.Flux;

import java.util.UUID;

public interface FindActivitiesBySiteUseCasePort {
    Flux<Activity> handle(UUID siteId);
}
