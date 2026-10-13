package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateEventRequest(
        @NotBlank String name,
        @NotBlank String category,
        @NotNull @DecimalMin(value = "0.0") BigDecimal price,
        @Min(1) int maxCapacity,
        @NotNull Instant startsAt,
        @NotNull Instant endsAt,
        String description
) {
}
