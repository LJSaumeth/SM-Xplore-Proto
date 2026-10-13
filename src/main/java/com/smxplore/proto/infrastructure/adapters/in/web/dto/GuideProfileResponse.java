package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import com.smxplore.proto.domain.model.user.UserStatus;

import java.time.LocalDate;
import java.util.UUID;

public record GuideProfileResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        UserStatus status,
        String license,
        LocalDate startOperationDate
) {
}
