package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import com.smxplore.proto.domain.model.reservation.ReservationItemType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * El precio es opcional: para items SERVICE se toma del catálogo y el valor
 * enviado se ignora; para ACTIVITY y EVENT es obligatorio (lo valida el
 * dominio) hasta que existan sus catálogos.
 */
public record ReservationItemRequest(
        @NotNull UUID itemId,
        @NotNull ReservationItemType type,
        @DecimalMin(value = "0.0") @Digits(integer = 12, fraction = 2) BigDecimal price
) {
}
