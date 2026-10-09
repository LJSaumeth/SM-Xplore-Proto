package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.service.ServiceType;
import com.smxplore.proto.domain.model.types.Page;
import com.smxplore.proto.domain.model.types.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRepositoryPort {
    Optional<UUID> save(Service service);

    boolean changeStatus(UUID serviceId, ServiceStatus newStatus);

    Page<Service> findAllByStatusAndType(ServiceStatus status, ServiceType type, PageRequest request);

    Optional<Service> findById(UUID id);

    Page<Service> findAllByStatus(ServiceStatus status, PageRequest request);

    List<Service> findAllByProviderId(UUID providerId);

    List<Service> findAllByProviderName(String providerName);

    Page<Service> findAll(PageRequest request);

    boolean changePrice(UUID serviceId, BigDecimal newPrice);
}
