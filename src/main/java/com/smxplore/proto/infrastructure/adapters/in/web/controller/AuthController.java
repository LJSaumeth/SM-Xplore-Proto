package com.smxplore.proto.infrastructure.adapters.in.web.controller;

import com.smxplore.proto.domain.ports.usecases.LoginUseCasePort;
import com.smxplore.proto.domain.ports.usecases.RecoverPasswordUseCasePort;
import com.smxplore.proto.domain.ports.usecases.RegisterUserUseCasePort;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.LoginRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.RecoverPasswordRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.RegisterUserRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.UserResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.UserWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login and password recovery")
public class AuthController {

    private final RegisterUserUseCasePort registerUserUseCase;
    private final LoginUseCasePort loginUseCase;
    private final RecoverPasswordUseCasePort recoverPasswordUseCase;
    private final UserWebMapper mapper;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user account")
    public Mono<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        return registerUserUseCase.handle(mapper.toDomain(request), request.password())
                .map(mapper::toResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in with email and password")
    public Mono<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        return loginUseCase.handle(request.email(), request.password())
                .map(mapper::toResponse);
    }

    @PostMapping("/recover-password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Request a password recovery")
    public Mono<Void> recoverPassword(@Valid @RequestBody RecoverPasswordRequest request) {
        return recoverPasswordUseCase.handle(request.email());
    }
}
