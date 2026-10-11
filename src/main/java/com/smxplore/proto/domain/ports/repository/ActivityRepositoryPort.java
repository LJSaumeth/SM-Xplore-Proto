package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.activity.Activity;
import com.smxplore.proto.domain.model.activity.ActivityStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ActivityRepositoryPort {

    Mono<Activity> save(Activity activity);

    Mono<Activity> findById(UUID id);

    Mono<Boolean> existsById(UUID id);

    Flux<Activity> findAll();

    Flux<Activity> findAllByStatus(ActivityStatus status);

    Flux<Activity> findAllByCategory(String category);

    Flux<Activity> findAllBySiteId(UUID siteId);

    Flux<Activity> findAllByGuideId(UUID guideId);
}
