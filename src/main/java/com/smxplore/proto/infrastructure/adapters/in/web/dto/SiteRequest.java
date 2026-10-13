package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record SiteRequest(
        @NotNull UUID zoneId,
        @NotBlank String name,
        @NotNull Double latitude,
        @NotNull Double longitude,
        List<String> photoUrls
) {
}
