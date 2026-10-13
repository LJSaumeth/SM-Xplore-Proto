package com.smxplore.proto.infrastructure.adapters.out.postgres.entity;

import com.smxplore.proto.domain.model.user.DniType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table("guides")
public record GuideEntity(
        @Id UUID id,
        String dniNumber,
        DniType dniType,
        String license,
        LocalDate startOperationDate
) {
}
