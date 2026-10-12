package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateActivityRequest(
        @NotBlank String name,
        @NotBlank String category,
        String description,
        @NotNull @DecimalMin(value = "0.0") BigDecimal price
) {
}
