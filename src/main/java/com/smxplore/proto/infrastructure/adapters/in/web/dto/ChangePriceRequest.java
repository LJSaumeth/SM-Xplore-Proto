package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ChangePriceRequest(
        @NotNull @DecimalMin(value = "0.0") BigDecimal price
) {
}
