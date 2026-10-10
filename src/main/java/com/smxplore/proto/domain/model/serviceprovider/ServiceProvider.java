package com.smxplore.proto.domain.model.serviceprovider;

import com.smxplore.proto.domain.exceptions.InvalidUserRoleException;
import com.smxplore.proto.domain.exceptions.ServiceProviderRegistrationNotPendingException;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserValidation;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@SuperBuilder
public class ServiceProvider extends User {

    private final String nit;
    private String serviceType;
    private RegistrationStatus registrationStatus;
    private Double latitude;
    private Double longitude;
    private LocalDate startOperationDate;

    @Override
    public void validate() {
        super.validate();
        if (getRole() != UserRole.SERVICE_PROVIDER) {
            throw new InvalidUserRoleException("A ServiceProvider must have the role SERVICE_PROVIDER.");
        }
        UserValidation.requireText(nit, "nit");
        UserValidation.requireNonNull(registrationStatus, "registrationStatus");
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
