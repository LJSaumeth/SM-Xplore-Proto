package com.smxplore.proto.infrastructure.adapters.in.web.mapper;

import com.smxplore.proto.domain.model.zone.Zone;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ZoneRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ZoneResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ZoneWebMapper {

    public ZoneResponse toResponse(Zone zone) {
        return new ZoneResponse(zone.getId(), zone.getName());
    }

    public Zone toDomain(ZoneRequest request) {
        return new Zone(UUID.randomUUID(), request.name());
    }
}
