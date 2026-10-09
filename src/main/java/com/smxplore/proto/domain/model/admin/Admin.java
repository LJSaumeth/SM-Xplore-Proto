package com.smxplore.proto.domain.model.admin;

import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.model.user.UserValidation;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Admin extends User {

    private final String dniNumber;
    private final DniType dniType;
    private LocalDateTime lastAccessDate;

    public Admin(
            UUID id,
            String fullName,
            String email,
            String phone,
            String passwordHash,
            UserStatus status,
            Instant signedUpAt,
            String dniNumber,
            DniType dniType,
            LocalDateTime lastAccessDate) {
        super(id, fullName, email, phone, passwordHash, UserRole.ADMIN, status, signedUpAt);
        this.dniNumber = UserValidation.requireText(dniNumber, "dniNumber");
        this.dniType = UserValidation.requireNonNull(dniType, "dniType");
        this.lastAccessDate = lastAccessDate;
    }

    public void registerAccess(LocalDateTime accessDate) {
        this.lastAccessDate = UserValidation.requireNonNull(accessDate, "lastAccessDate");
    }
}
