package com.smxplore.proto.domain.model.destinationmanager;

import com.smxplore.proto.domain.exceptions.InvalidUserRoleException;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserValidation;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@SuperBuilder
public class DestinationManager extends User {

    private final String nit;
    private LocalDate startOperationDate;

    @Override
    public void validate() {
        super.validate();
        if (getRole() != UserRole.DESTINATION_MANAGER) {
            throw new InvalidUserRoleException("A DestinationManager must have the role DESTINATION_MANAGER.");
        }
        UserValidation.requireText(nit, "nit");
    }

    public void updateOperationDate(LocalDate startOperationDate) {
        this.startOperationDate = UserValidation.requireNonNull(startOperationDate, "startOperationDate");
    }
}
