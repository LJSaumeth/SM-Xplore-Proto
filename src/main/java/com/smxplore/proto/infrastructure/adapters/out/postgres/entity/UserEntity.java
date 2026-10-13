package com.smxplore.proto.infrastructure.adapters.out.postgres.entity;

import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("users")
public record UserEntity(
        @Id UUID id,
        String fullName,
        String email,
        String phone,
        String passwordHash,
        UserRole role,
        UserStatus status,
        Instant signedUpAt
) {
}
