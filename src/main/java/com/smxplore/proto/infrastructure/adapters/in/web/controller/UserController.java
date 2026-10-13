package com.smxplore.proto.infrastructure.adapters.in.web.controller;

import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.ports.repository.UserSearchCriteria;
import com.smxplore.proto.domain.ports.usecases.AdminDeleteUserAccountUseCasePort;
import com.smxplore.proto.domain.ports.usecases.AssignUserRoleUseCasePort;
import com.smxplore.proto.domain.ports.usecases.BlockOrSuspendUserUseCasePort;
import com.smxplore.proto.domain.ports.usecases.DeleteOwnAccountUseCasePort;
import com.smxplore.proto.domain.ports.usecases.GetUserProfileUseCasePort;
import com.smxplore.proto.domain.ports.usecases.ListUsersUseCasePort;
import com.smxplore.proto.domain.ports.usecases.ResetUserPasswordUseCasePort;
import com.smxplore.proto.domain.ports.usecases.SearchUserUseCasePort;
import com.smxplore.proto.domain.ports.usecases.UpdateUserProfileUseCasePort;
import com.smxplore.proto.domain.ports.usecases.ViewGuideProfileUseCasePort;
import com.smxplore.proto.domain.ports.usecases.ViewServiceProviderProfileUseCasePort;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.AssignRoleRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.BlockUserRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.GuideProfileResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ResetPasswordRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ServiceProviderProfileResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.UpdateUserProfileRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.UserResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.UserWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User accounts management")
public class UserController {

    private final GetUserProfileUseCasePort getUserProfileUseCase;
    private final UpdateUserProfileUseCasePort updateUserProfileUseCase;
    private final DeleteOwnAccountUseCasePort deleteOwnAccountUseCase;
    private final AdminDeleteUserAccountUseCasePort adminDeleteUserAccountUseCase;
    private final ListUsersUseCasePort listUsersUseCase;
    private final SearchUserUseCasePort searchUserUseCase;
    private final AssignUserRoleUseCasePort assignUserRoleUseCase;
    private final BlockOrSuspendUserUseCasePort blockOrSuspendUserUseCase;
    private final ResetUserPasswordUseCasePort resetUserPasswordUseCase;
    private final ViewGuideProfileUseCasePort viewGuideProfileUseCase;
    private final ViewServiceProviderProfileUseCasePort viewServiceProviderProfileUseCase;
    private final UserWebMapper mapper;

    @GetMapping("/{userId}")
    @Operation(summary = "Get a user profile by id")
    public Mono<UserResponse> findOne(@PathVariable UUID userId) {
        return getUserProfileUseCase.handle(userId).map(mapper::toResponse);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update a user profile")
    public Mono<UserResponse> update(@PathVariable UUID userId,
                                     @Valid @RequestBody UpdateUserProfileRequest request) {
        return updateUserProfileUseCase.handle(userId, request.fullName(), request.email(), request.phone())
                .map(mapper::toResponse);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete your own account")
    public Mono<Void> deleteOwnAccount(@PathVariable UUID userId) {
        return deleteOwnAccountUseCase.handle(userId);
    }

    @GetMapping
    @Operation(summary = "List all users")
    public Flux<UserResponse> findAll() {
        return listUsersUseCase.handle().map(mapper::toResponse);
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by role, status or term")
    public Flux<UserResponse> search(@RequestParam(required = false) UserRole role,
                                     @RequestParam(required = false) UserStatus status,
                                     @RequestParam(required = false) String term) {
        return searchUserUseCase.handle(new UserSearchCriteria(role, status, term)).map(mapper::toResponse);
    }

    @PatchMapping("/{userId}/role")
    @Operation(summary = "Assign a role to a user")
    public Mono<UserResponse> assignRole(@PathVariable UUID userId,
                                         @Valid @RequestBody AssignRoleRequest request) {
        return assignUserRoleUseCase.handle(userId, request.role()).map(mapper::toResponse);
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "Block or suspend a user account")
    public Mono<UserResponse> changeStatus(@PathVariable UUID userId,
                                           @Valid @RequestBody BlockUserRequest request) {
        return blockOrSuspendUserUseCase.handle(userId, request.status()).map(mapper::toResponse);
    }

    @DeleteMapping("/{userId}/admin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a user account (admin)")
    public Mono<Void> adminDelete(@PathVariable UUID userId) {
        return adminDeleteUserAccountUseCase.handle(userId);
    }

    @PostMapping("/{userId}/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Reset a user password (admin)")
    public Mono<Void> resetPassword(@PathVariable UUID userId,
                                    @Valid @RequestBody ResetPasswordRequest request) {
        return resetUserPasswordUseCase.handle(userId, request.password());
    }

    @GetMapping("/guides/{guideId}")
    @Operation(summary = "Get a guide profile")
    public Mono<GuideProfileResponse> findGuide(@PathVariable UUID guideId) {
        return viewGuideProfileUseCase.handle(guideId).map(mapper::toGuideResponse);
    }

    @GetMapping("/providers/{providerId}")
    @Operation(summary = "Get a service provider profile")
    public Mono<ServiceProviderProfileResponse> findServiceProvider(@PathVariable UUID providerId) {
        return viewServiceProviderProfileUseCase.handle(providerId).map(mapper::toServiceProviderResponse);
    }
}
