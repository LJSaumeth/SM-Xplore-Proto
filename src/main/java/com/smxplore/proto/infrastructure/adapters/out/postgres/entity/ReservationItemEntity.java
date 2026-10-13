package com.smxplore.proto.infrastructure.adapters.out.postgres.entity;

import com.smxplore.proto.domain.model.reservation.ReservationItemType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("reservation_items")
public record ReservationItemEntity(
        @Id
        UUID id,
        UUID reservationId,
        UUID itemId,
        ReservationItemType type,
        BigDecimal price
) {
}
