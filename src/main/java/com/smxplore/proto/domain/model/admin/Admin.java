package com.smxplore.proto.domain.model.admin;

import com.smxplore.proto.domain.exceptions.InvalidUserRoleException;
import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserValidation;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
public class Admin extends User {

    private final String dniNumber;
    private final DniType dniType;
    private Instant lastAccessDate;

    @Override
    public void validate() {
        super.validate();
        if (getRole() != UserRole.ADMIN) {
            throw new InvalidUserRoleException("An Admin must have the role ADMIN.");
        }
        UserValidation.requireText(dniNumber, "dniNumber");
        UserValidation.requireNonNull(dniType, "dniType");
    }

    public void registerAccess(Instant accessDate) {
        this.lastAccessDate = UserValidation.requireNonNull(accessDate, "lastAccessDate");
    }
}
