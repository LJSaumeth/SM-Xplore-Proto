package com.smxplore.proto.domain.model.tourist;

import com.smxplore.proto.domain.exceptions.InvalidUserRoleException;
import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserValidation;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@SuperBuilder
public class Tourist extends User {

    private final String dniNumber;
    private final DniType dniType;
    private String preferredLanguage;
    private LocalDate birthDate;
    private String nationality;

    @Override
    public void validate() {
        super.validate();
        if (getRole() != UserRole.TOURIST) {
            throw new InvalidUserRoleException("A Tourist must have the role TOURIST.");
        }
        UserValidation.requireText(dniNumber, "dniNumber");
        UserValidation.requireNonNull(dniType, "dniType");
    }

    public void updateTouristDetails(String preferredLanguage, LocalDate birthDate, String nationality) {
        this.preferredLanguage = preferredLanguage;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }
}
