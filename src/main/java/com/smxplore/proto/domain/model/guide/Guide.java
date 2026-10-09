package com.smxplore.proto.domain.model.guide;

import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.model.user.UserValidation;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class Guide extends User {

    private final String dniNumber;
    private final DniType dniType;
    private final String license;
    private LocalDate startOperationDate;

    public Guide(
            UUID id,
            String fullName,
            String email,
            String phone,
            String passwordHash,
            UserStatus status,
            Instant signedUpAt,
            String dniNumber,
            DniType dniType,
            String license,
            LocalDate startOperationDate) {
        super(id, fullName, email, phone, passwordHash, UserRole.GUIDE, status, signedUpAt);
        this.dniNumber = UserValidation.requireText(dniNumber, "dniNumber");
        this.dniType = UserValidation.requireNonNull(dniType, "dniType");
        this.license = UserValidation.requireText(license, "license");
        this.startOperationDate = startOperationDate;
    }

    public void updateOperationDate(LocalDate startOperationDate) {
        this.startOperationDate = UserValidation.requireNonNull(startOperationDate, "startOperationDate");
    }
}
