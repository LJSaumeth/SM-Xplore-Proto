package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.serviceprovider.ServiceProvider;

import java.util.UUID;

public interface ViewServiceProviderProfileUseCasePort {

    ServiceProvider handle(UUID serviceProviderId);
}
