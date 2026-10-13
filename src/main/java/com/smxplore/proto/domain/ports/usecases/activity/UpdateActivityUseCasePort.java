package com.smxplore.proto.domain.ports.usecases.activity;

import com.smxplore.proto.domain.model.activity.Activity;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateActivityUseCasePort {
    Mono<Activity> handle(UUID activityId, String name, String category, String description, BigDecimal price);
}
