package com.smxplore.proto.domain.ports.usecases.securityreport;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.model.securityreport.SecurityReportStatus;
import com.smxplore.proto.domain.model.types.Page;
import com.smxplore.proto.domain.model.types.PageRequest;

public interface ConsultReportsUseCasePort {
    Page<SecurityReport> handle(SecurityReportStatus status, PageRequest pageRequest);
}
