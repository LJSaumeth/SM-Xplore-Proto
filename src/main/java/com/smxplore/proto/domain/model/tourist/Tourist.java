package com.smxplore.proto.domain.model.tourist;

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
public class Tourist extends User {

    private final String dniNumber;
    private final DniType dniType;
    private String preferredLanguage;
    private LocalDate birthDate;
    private String nationality;

    public Tourist(
            UUID id,
            String fullName,
            String email,
            String phone,
            String passwordHash,
            UserStatus status,
            Instant signedUpAt,
            String dniNumber,
            DniType dniType,
            String preferredLanguage,
            LocalDate birthDate,
            String nationality) {
        super(id, fullName, email, phone, passwordHash, UserRole.TOURIST, status, signedUpAt);
        this.dniNumber = UserValidation.requireText(dniNumber, "dniNumber");
        this.dniType = UserValidation.requireNonNull(dniType, "dniType");
        this.preferredLanguage = preferredLanguage;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }

    public void updateTouristDetails(String preferredLanguage, LocalDate birthDate, String nationality) {
        this.preferredLanguage = preferredLanguage;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }
}
