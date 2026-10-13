package com.smxplore.proto.infrastructure.adapters.out.postgres.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("zona")
public record ZoneEntity(
        @Id UUID id,
        String name
) {
}
