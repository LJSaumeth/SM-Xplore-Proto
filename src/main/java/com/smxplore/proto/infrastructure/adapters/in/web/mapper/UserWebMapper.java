package com.smxplore.proto.infrastructure.adapters.in.web.mapper;

import com.smxplore.proto.domain.model.admin.Admin;
import com.smxplore.proto.domain.model.destinationmanager.DestinationManager;
import com.smxplore.proto.domain.model.guide.Guide;
import com.smxplore.proto.domain.model.serviceprovider.RegistrationStatus;
import com.smxplore.proto.domain.model.serviceprovider.ServiceProvider;
import com.smxplore.proto.domain.model.tourist.Tourist;
import com.smxplore.proto.domain.model.user.LoginResult;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.AuthResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.GuideProfileResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.RegisterUserRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ServiceProviderProfileResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.UserResponse;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserWebMapper {

    UserResponse toResponse(User user);

    default AuthResponse toAuthResponse(LoginResult result) {
        return new AuthResponse(result.token(), "Bearer", result.expiresInSeconds(), toResponse(result.user()));
    }

    GuideProfileResponse toGuideResponse(Guide guide);

    ServiceProviderProfileResponse toServiceProviderResponse(ServiceProvider serviceProvider);

    default User toDomain(RegisterUserRequest request) {
        var id = UUID.randomUUID();
        var now = Instant.now();
        return switch (request.role()) {
            case TOURIST -> toTourist(request, id, now);
            case GUIDE -> toGuide(request, id, now);
            case ADMIN -> toAdmin(request, id, now);
            case DESTINATION_MANAGER -> toDestinationManager(request, id, now);
            case SERVICE_PROVIDER -> toServiceProvider(request, id, now);
        };
    }

    private static Tourist toTourist(RegisterUserRequest request, UUID id, Instant now) {
        return Tourist.builder()
                .id(id)
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .role(UserRole.TOURIST)
                .status(UserStatus.ACTIVE)
                .signedUpAt(now)
                .dniNumber(request.dniNumber())
                .dniType(request.dniType())
                .preferredLanguage(request.preferredLanguage())
                .birthDate(request.birthDate())
                .nationality(request.nationality())
                .build();
    }

    private static Guide toGuide(RegisterUserRequest request, UUID id, Instant now) {
        return Guide.builder()
                .id(id)
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .role(UserRole.GUIDE)
                .status(UserStatus.ACTIVE)
                .signedUpAt(now)
                .dniNumber(request.dniNumber())
                .dniType(request.dniType())
                .license(request.license())
                .startOperationDate(request.startOperationDate())
                .build();
    }

    private static Admin toAdmin(RegisterUserRequest request, UUID id, Instant now) {
        return Admin.builder()
                .id(id)
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .signedUpAt(now)
                .dniNumber(request.dniNumber())
                .dniType(request.dniType())
                .build();
    }

    private static DestinationManager toDestinationManager(RegisterUserRequest request, UUID id, Instant now) {
        return DestinationManager.builder()
                .id(id)
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .role(UserRole.DESTINATION_MANAGER)
                .status(UserStatus.ACTIVE)
                .signedUpAt(now)
                .nit(request.nit())
                .startOperationDate(request.startOperationDate())
                .build();
    }

    private static ServiceProvider toServiceProvider(RegisterUserRequest request, UUID id, Instant now) {
        return ServiceProvider.builder()
                .id(id)
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .role(UserRole.SERVICE_PROVIDER)
                .status(UserStatus.INACTIVE)
                .signedUpAt(now)
                .nit(request.nit())
                .serviceType(request.serviceType())
                .registrationStatus(RegistrationStatus.PENDING)
                .latitude(request.latitude())
                .longitude(request.longitude())
                .startOperationDate(request.startOperationDate())
                .build();
    }
}
