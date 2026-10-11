package com.smxplore.proto.domain.ports.usecases.activity;

import com.smxplore.proto.domain.model.activity.Activity;

import reactor.core.publisher.Mono;

public interface RegisterActivityUseCasePort {
    Mono<Activity> handle(Activity activity);
}
