package com.smxplore.proto.domain.ports.usecases;

public record LoginCommand(
        String email,
        String password) {
}
