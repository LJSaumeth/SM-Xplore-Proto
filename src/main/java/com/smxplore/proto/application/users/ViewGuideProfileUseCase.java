package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.exceptions.UserNotFoundException;
import com.smxplore.proto.domain.model.guide.Guide;
import com.smxplore.proto.domain.ports.repository.GuideRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.ViewGuideProfileUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class ViewGuideProfileUseCase implements ViewGuideProfileUseCasePort {

    private final GuideRepositoryPort guideRepository;

    @Override
    public Mono<Guide> handle(UUID guideId) {
        return guideRepository.findById(guideId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(guideId)));
    }
}
