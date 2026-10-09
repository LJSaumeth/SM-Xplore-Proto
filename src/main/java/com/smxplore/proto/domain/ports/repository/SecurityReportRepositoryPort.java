package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.model.securityreport.SecurityReportStatus;
import com.smxplore.proto.domain.model.types.Page;
import com.smxplore.proto.domain.model.types.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SecurityReportRepositoryPort {
    Optional<UUID> save(SecurityReport securityReport);

    Optional<SecurityReport> findById(UUID id);

    Page<SecurityReport> findAllByStatus(SecurityReportStatus status, PageRequest pageRequest);

    Page<SecurityReport> findAll(PageRequest pageRequest);

    void changeText(UUID reportId, String content);

    void changeStatus(UUID reportId, SecurityReportStatus status);

    List<SecurityReport> findAllByUserId(UUID userId);
}
