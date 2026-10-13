package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.guide.Guide;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ApproveGuideRegistrationUseCasePort {

    Mono<Guide> handle(UUID guideId);
}
