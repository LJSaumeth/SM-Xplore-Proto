package com.smxplore.proto.infrastructure.config;

import com.smxplore.proto.application.sites.FindSiteUseCase;
import com.smxplore.proto.application.sites.ListSitesUseCase;
import com.smxplore.proto.application.sites.RegisterSiteUseCase;
import com.smxplore.proto.application.sites.UpdateSiteUseCase;
import com.smxplore.proto.application.zones.FindZoneUseCase;
import com.smxplore.proto.application.zones.ListZonesUseCase;
import com.smxplore.proto.application.zones.RegisterZoneUseCase;
import com.smxplore.proto.application.zones.RenameZoneUseCase;
import com.smxplore.proto.domain.ports.repository.ISiteRepository;
import com.smxplore.proto.domain.ports.repository.IZoneRepository;
import com.smxplore.proto.domain.ports.usecases.site.FindSiteUseCasePort;
import com.smxplore.proto.domain.ports.usecases.site.ListSitesUseCasePort;
import com.smxplore.proto.domain.ports.usecases.site.RegisterSiteUseCasePort;
import com.smxplore.proto.domain.ports.usecases.site.UpdateSiteUseCasePort;
import com.smxplore.proto.domain.ports.usecases.zone.FindZoneUseCasePort;
import com.smxplore.proto.domain.ports.usecases.zone.ListZonesUseCasePort;
import com.smxplore.proto.domain.ports.usecases.zone.RegisterZoneUseCasePort;
import com.smxplore.proto.domain.ports.usecases.zone.RenameZoneUseCasePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SiteZoneUseCaseConfiguration {

    @Bean
    public RegisterZoneUseCasePort registerZoneUseCase(IZoneRepository zoneRepository) {
        return new RegisterZoneUseCase(zoneRepository);
    }

    @Bean
    public RenameZoneUseCasePort renameZoneUseCase(IZoneRepository zoneRepository) {
        return new RenameZoneUseCase(zoneRepository);
    }

    @Bean
    public FindZoneUseCasePort findZoneUseCase(IZoneRepository zoneRepository) {
        return new FindZoneUseCase(zoneRepository);
    }

    @Bean
    public ListZonesUseCasePort listZonesUseCase(IZoneRepository zoneRepository) {
        return new ListZonesUseCase(zoneRepository);
    }

    @Bean
    public RegisterSiteUseCasePort registerSiteUseCase(ISiteRepository siteRepository,
                                                       IZoneRepository zoneRepository) {
        return new RegisterSiteUseCase(siteRepository, zoneRepository);
    }

    @Bean
    public UpdateSiteUseCasePort updateSiteUseCase(ISiteRepository siteRepository,
                                                   IZoneRepository zoneRepository) {
        return new UpdateSiteUseCase(siteRepository, zoneRepository);
    }

    @Bean
    public FindSiteUseCasePort findSiteUseCase(ISiteRepository siteRepository) {
        return new FindSiteUseCase(siteRepository);
    }

    @Bean
    public ListSitesUseCasePort listSitesUseCase(ISiteRepository siteRepository) {
        return new ListSitesUseCase(siteRepository);
    }
}
