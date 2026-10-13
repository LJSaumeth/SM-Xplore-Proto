package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegisterUserRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String phone,
        @NotBlank String password,
        @NotNull UserRole role,
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
        LocalDate startOperationDate
) {
}
