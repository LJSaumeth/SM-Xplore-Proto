package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.serviceprovider.ServiceProvider;

import java.util.UUID;

public interface ApproveServiceProviderRegistrationUseCasePort {

    ServiceProvider handle(UUID serviceProviderId);
}
