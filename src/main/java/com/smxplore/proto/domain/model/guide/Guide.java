package com.smxplore.proto.domain.model.guide;

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
public class Guide extends User {

    private final String dniNumber;
    private final DniType dniType;
    private final String license;
    private LocalDate startOperationDate;

    @Override
    public void validate() {
        super.validate();
        if (getRole() != UserRole.GUIDE) {
            throw new InvalidUserRoleException("A Guide must have the role GUIDE.");
        }
        UserValidation.requireText(dniNumber, "dniNumber");
        UserValidation.requireNonNull(dniType, "dniType");
        UserValidation.requireText(license, "license");
    }

    public void updateOperationDate(LocalDate startOperationDate) {
        this.startOperationDate = UserValidation.requireNonNull(startOperationDate, "startOperationDate");
    }
}
