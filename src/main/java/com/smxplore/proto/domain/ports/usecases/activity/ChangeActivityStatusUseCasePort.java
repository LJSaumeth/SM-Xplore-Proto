package com.smxplore.proto.domain.ports.usecases.activity;

import com.smxplore.proto.domain.model.activity.Activity;
import com.smxplore.proto.domain.model.activity.ActivityStatus;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ChangeActivityStatusUseCasePort {
    Mono<Activity> handle(UUID activityId, ActivityStatus newStatus);
}
