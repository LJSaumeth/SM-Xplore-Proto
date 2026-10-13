package com.smxplore.proto.application.zones;

import com.smxplore.proto.domain.model.zone.Zone;
import com.smxplore.proto.domain.ports.repository.IZoneRepository;
import com.smxplore.proto.domain.ports.usecases.zone.ListZonesUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ListZonesUseCase implements ListZonesUseCasePort {

    private final IZoneRepository zoneRepository;

    @Override
    public Flux<Zone> handle() {
        return zoneRepository.findAll();
    }
}
