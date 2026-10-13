package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserProfileRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String phone
) {
}
