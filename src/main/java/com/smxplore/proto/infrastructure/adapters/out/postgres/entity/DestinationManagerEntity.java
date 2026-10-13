package com.smxplore.proto.infrastructure.adapters.out.postgres.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table("destination_managers")
public record DestinationManagerEntity(
        @Id UUID id,
        String nit,
        LocalDate startOperationDate
) {
}
