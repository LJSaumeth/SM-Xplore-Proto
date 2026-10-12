package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record EventRequest(
        @NotNull UUID siteId,
        @NotBlank String name,
        @NotBlank String category,
        @NotNull @DecimalMin(value = "0.0") BigDecimal price,
        @NotNull @Positive Integer maxCapacity,
        @NotNull Instant startsAt,
        @NotNull Instant endsAt,
        String description
) {
}
