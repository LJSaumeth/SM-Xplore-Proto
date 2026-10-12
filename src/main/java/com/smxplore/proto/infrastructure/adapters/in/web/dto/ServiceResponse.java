package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.service.ServiceType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ServiceResponse(
        UUID id,
        String name,
        ServiceType type,
        ProviderRefDto provider,
        LocationDto location,
        BigDecimal price,
        List<String> photosUrls,
        ServiceStatus status,
        Instant createdAt,
        List<AttributeDto> extras
) {
}
