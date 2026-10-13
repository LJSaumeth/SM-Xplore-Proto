package com.smxplore.proto.domain.model.user;

public record LoginResult(User user, String token, long expiresInSeconds) {
}
