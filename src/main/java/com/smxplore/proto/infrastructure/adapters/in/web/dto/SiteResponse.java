package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import java.util.List;
import java.util.UUID;

public record SiteResponse(
        UUID id,
        UUID zoneId,
        String name,
        Double latitude,
        Double longitude,
        List<String> photoUrls
) {
}
