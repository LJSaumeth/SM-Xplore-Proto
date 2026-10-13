package com.smxplore.proto.infrastructure.adapters.out.postgres.entity;

import com.smxplore.proto.domain.model.serviceprovider.RegistrationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table("service_providers")
public record ServiceProviderEntity(
        @Id UUID id,
        String nit,
        String serviceType,
        RegistrationStatus registrationStatus,
        Double latitude,
        Double longitude,
        LocalDate startOperationDate
) {
}
