package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotNull;

public record LocationDto(
        @NotNull Double latitude,
        @NotNull Double longitude
) {
}
