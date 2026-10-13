package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import com.smxplore.proto.domain.model.serviceprovider.RegistrationStatus;
import com.smxplore.proto.domain.model.user.UserStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ServiceProviderProfileResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        UserStatus status,
        String nit,
        String serviceType,
        RegistrationStatus registrationStatus,
        Double latitude,
        Double longitude,
        LocalDate startOperationDate
) {
}
