package com.smxplore.proto.domain.model.destinationmanager;

import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.model.user.UserValidation;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class DestinationManager extends User {

    private final String nit;
    private LocalDate startOperationDate;

    public DestinationManager(
            UUID id,
            String fullName,
            String email,
            String phone,
            String passwordHash,
            UserStatus status,
            Instant signedUpAt,
            String nit,
            LocalDate startOperationDate) {
        super(id, fullName, email, phone, passwordHash, UserRole.DESTINATION_MANAGER, status, signedUpAt);
        this.nit = UserValidation.requireText(nit, "nit");
        this.startOperationDate = startOperationDate;
    }

    public void updateOperationDate(LocalDate startOperationDate) {
        this.startOperationDate = UserValidation.requireNonNull(startOperationDate, "startOperationDate");
    }
}
