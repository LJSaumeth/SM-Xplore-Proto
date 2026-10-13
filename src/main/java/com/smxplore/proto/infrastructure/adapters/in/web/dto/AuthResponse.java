package com.smxplore.proto.infrastructure.adapters.in.web.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn,
        UserResponse user
) {
}
