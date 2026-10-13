package com.smxplore.proto.application.activities;

import com.smxplore.proto.domain.model.activity.Activity;
import com.smxplore.proto.domain.model.site.Site;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ISiteRepository;
import com.smxplore.proto.domain.ports.usecases.activity.FindActivitiesByZoneUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class FindActivitiesByZoneUseCase implements FindActivitiesByZoneUseCasePort {

    private final ActivityRepositoryPort activityRepository;
    private final ISiteRepository siteRepository;

    @Override
    public Flux<Activity> handle(UUID zoneId) {
        return siteRepository.findAllByZoneId(zoneId)
                .map(Site::getId)
                .collectList()
                .flatMapMany(siteIds -> siteIds.isEmpty()
                        ? Flux.empty()
                        : activityRepository.findAllBySiteIdIn(siteIds));
    }
}
