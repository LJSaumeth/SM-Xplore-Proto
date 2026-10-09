package com.smxplore.proto.domain.model.user;

import com.smxplore.proto.domain.exceptions.UserAccountNotActiveException;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class User {

    private final UUID id;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private UserRole role;
    private UserStatus status;
    private final Instant signedUpAt;

    protected User(
            UUID id,
            String fullName,
            String email,
            String phone,
            String passwordHash,
            UserRole role,
            UserStatus status,
            Instant signedUpAt) {
        this.id = UserValidation.requireId(id);
        this.signedUpAt = UserValidation.requireInstant(signedUpAt, "signedUpAt");
        this.fullName = UserValidation.validateFullName(fullName);
        this.email = UserValidation.validateEmail(email);
        this.phone = UserValidation.validatePhone(phone);
        this.passwordHash = UserValidation.validatePasswordHash(passwordHash);
        this.role = UserValidation.requireNonNull(role, "role");
        this.status = UserValidation.requireNonNull(status, "status");
    }

    public void updateProfile(String fullName, String email, String phone) {
        this.fullName = UserValidation.validateFullName(fullName);
        this.email = UserValidation.validateEmail(email);
        this.phone = UserValidation.validatePhone(phone);
    }

    public void changeRole(UserRole newRole) {
        this.role = UserValidation.requireNonNull(newRole, "role");
    }

    public void changePasswordHash(String newPasswordHash) {
        this.passwordHash = UserValidation.validatePasswordHash(newPasswordHash);
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void block() {
        this.status = UserStatus.BLOCKED;
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public void ensureActive() {
        if (!isActive()) {
            throw new UserAccountNotActiveException(this.id, this.status);
        }
    }
}
