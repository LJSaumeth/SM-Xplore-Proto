package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import com.smxplore.proto.domain.model.reservation.PaymentMethod;
import com.smxplore.proto.domain.model.reservation.ReservationStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        UUID touristId,
        int numberOfPeople,
        BigDecimal total,
        PaymentMethod paymentMethod,
        ReservationStatus status,
        Instant bookedAt,
        Instant bookedFor,
        String notes
) {
}
