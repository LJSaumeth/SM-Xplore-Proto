package com.smxplore.proto.infrastructure.adapters.out.postgres.entity;

import com.smxplore.proto.domain.model.reservation.PaymentMethod;
import com.smxplore.proto.domain.model.reservation.ReservationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table("reservations")
public record ReservationEntity(
        @Id
        UUID id,
        UUID touristId,
        int numberOfPeople,
        BigDecimal total,
        PaymentMethod paymentMethod,
        ReservationStatus status,
        Instant bookedAt,
        Instant bookedFor,
        String notes,
        UUID providerId,
        UUID guideId,
        String code
) {
}
