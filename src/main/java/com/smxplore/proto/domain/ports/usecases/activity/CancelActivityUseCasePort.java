package com.smxplore.proto.domain.ports.usecases.activity;

import com.smxplore.proto.domain.model.activity.Activity;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CancelActivityUseCasePort {
    Mono<Activity> handle(UUID activityId);
}
