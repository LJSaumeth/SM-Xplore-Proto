package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.guide.Guide;

import java.util.UUID;

public interface ViewGuideProfileUseCasePort {

    Guide handle(UUID guideId);
}
