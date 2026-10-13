package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import java.util.List;
import java.util.UUID;

/** Partial update: every field is optional and only present fields are applied. */
public record UpdateSiteRequest(
        String name,
        Double latitude,
        Double longitude,
        UUID zoneId,
        List<String> photoUrls
) {
}
