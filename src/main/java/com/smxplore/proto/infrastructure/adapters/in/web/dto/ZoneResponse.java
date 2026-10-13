package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import java.util.UUID;

public record ZoneResponse(
        UUID id,
        String name
) {
}
