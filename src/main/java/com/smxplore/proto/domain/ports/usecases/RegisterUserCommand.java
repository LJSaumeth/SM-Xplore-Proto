package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.UserRole;

import java.time.LocalDate;
import java.util.UUID;

public record RegisterUserCommand(
        UUID id,
        String fullName,
        String email,
        String phone,
        String password,
        UserRole role,
        String dniNumber,
        DniType dniType,
        String license,
        String nit,
        String serviceType,
        Double latitude,
        Double longitude,
        String preferredLanguage,
        LocalDate birthDate,
        String nationality,
        LocalDate startOperationDate) {
}
