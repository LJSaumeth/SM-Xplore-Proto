package com.smxplore.proto.domain.model.serviceprovider;

import com.smxplore.proto.domain.exceptions.ServiceProviderRegistrationNotPendingException;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.model.user.UserValidation;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class ServiceProvider extends User {

    private final String nit;
    private String serviceType;
    private RegistrationStatus registrationStatus;
    private Double latitude;
    private Double longitude;
    private LocalDate startOperationDate;

    public ServiceProvider(
            UUID id,
            String fullName,
            String email,
            String phone,
            String passwordHash,
            UserStatus status,
            Instant signedUpAt,
            String nit,
            String serviceType,
            RegistrationStatus registrationStatus,
            Double latitude,
            Double longitude,
            LocalDate startOperationDate) {
        super(id, fullName, email, phone, passwordHash, UserRole.SERVICE_PROVIDER, status, signedUpAt);
        this.nit = UserValidation.requireText(nit, "nit");
        this.serviceType = serviceType;
        this.registrationStatus = UserValidation.requireNonNull(registrationStatus, "registrationStatus");
        this.latitude = latitude;
        this.longitude = longitude;
        this.startOperationDate = startOperationDate;
    }

    public void updateProviderDetails(
            String serviceType,
            Double latitude,
            Double longitude,
            LocalDate startOperationDate) {
        this.serviceType = serviceType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startOperationDate = startOperationDate;
    }

    public void approveRegistration() {
        ensureRegistrationIsPending();
        this.registrationStatus = RegistrationStatus.APPROVED;
        this.activate();
    }

    public void rejectRegistration() {
        ensureRegistrationIsPending();
        this.registrationStatus = RegistrationStatus.REJECTED;
    }

    public boolean isRegistrationApproved() {
        return this.registrationStatus == RegistrationStatus.APPROVED;
    }

    private void ensureRegistrationIsPending() {
        if (this.registrationStatus != RegistrationStatus.PENDING) {
            throw new ServiceProviderRegistrationNotPendingException(getId());
        }
    }
}
