package com.smxplore.proto.infrastructure.adapters.out.postgres.entity;

import com.smxplore.proto.domain.model.user.DniType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("admins")
public record AdminEntity(
        @Id UUID id,
        String dniNumber,
        DniType dniType,
        Instant lastAccessDate
) {
}
