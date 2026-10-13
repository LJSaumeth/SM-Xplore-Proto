package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import com.smxplore.proto.domain.model.reservation.ReservationItemType;

import java.math.BigDecimal;
import java.util.UUID;

public record ReservationItemResponse(
        UUID id,
        UUID reservationId,
        UUID itemId,
        ReservationItemType type,
        BigDecimal price
) {
}
