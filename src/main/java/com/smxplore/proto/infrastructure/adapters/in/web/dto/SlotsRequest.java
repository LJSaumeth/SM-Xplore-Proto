package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SlotsRequest(
        @NotNull @Positive Integer maxCapacity
) {
}
