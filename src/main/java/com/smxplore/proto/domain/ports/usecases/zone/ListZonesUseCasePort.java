package com.smxplore.proto.domain.ports.usecases.zone;

import com.smxplore.proto.domain.model.zone.Zone;

import reactor.core.publisher.Flux;

public interface ListZonesUseCasePort {

    Flux<Zone> handle();
}
