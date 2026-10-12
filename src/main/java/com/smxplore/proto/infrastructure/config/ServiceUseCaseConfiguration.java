package com.smxplore.proto.infrastructure.config;

import com.smxplore.proto.application.services.AddAttributesToServiceUseCase;
import com.smxplore.proto.application.services.AddServicePhotosUseCase;
import com.smxplore.proto.application.services.ChangeServicePriceUseCase;
import com.smxplore.proto.application.services.FindServiceUseCase;
import com.smxplore.proto.application.services.FindServicesByProviderUseCase;
import com.smxplore.proto.application.services.FindServicesByStatusUseCase;
import com.smxplore.proto.application.services.FindServicesByTypeUseCase;
import com.smxplore.proto.application.services.MarkServiceAsAvailableUseCase;
import com.smxplore.proto.application.services.MarkServiceAsUnavailableUseCase;
import com.smxplore.proto.application.services.RegisterServiceUseCase;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.service.AddAttributeToServiceUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.AddServicePhotosUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.ChangeServicePriceUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.FindServiceUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.FindServicesByProviderUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.FindServicesByStatusUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.FindServicesByTypeUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.MarkServiceAsAvailableUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.MarkServiceAsUnavailableUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.RegisterServiceUseCasePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ServiceUseCaseConfiguration {

    @Bean
    public RegisterServiceUseCasePort registerServiceUseCase(ServiceRepositoryPort serviceRepository) {
        return new RegisterServiceUseCase(serviceRepository);
    }

    @Bean
    public FindServiceUseCasePort findServiceUseCase(ServiceRepositoryPort serviceRepository) {
        return new FindServiceUseCase(serviceRepository);
    }

    @Bean
    public FindServicesByStatusUseCasePort findServicesByStatusUseCase(ServiceRepositoryPort serviceRepository) {
        return new FindServicesByStatusUseCase(serviceRepository);
    }

    @Bean
    public FindServicesByTypeUseCasePort findServicesByTypeUseCase(ServiceRepositoryPort serviceRepository) {
        return new FindServicesByTypeUseCase(serviceRepository);
    }

    @Bean
    public FindServicesByProviderUseCasePort findServicesByProviderUseCase(ServiceRepositoryPort serviceRepository) {
        return new FindServicesByProviderUseCase(serviceRepository);
    }

    @Bean
    public ChangeServicePriceUseCasePort changeServicePriceUseCase(ServiceRepositoryPort serviceRepository) {
        return new ChangeServicePriceUseCase(serviceRepository);
    }

    @Bean
    public MarkServiceAsAvailableUseCasePort markServiceAsAvailableUseCase(ServiceRepositoryPort serviceRepository) {
        return new MarkServiceAsAvailableUseCase(serviceRepository);
    }

    @Bean
    public MarkServiceAsUnavailableUseCasePort markServiceAsUnavailableUseCase(ServiceRepositoryPort serviceRepository) {
        return new MarkServiceAsUnavailableUseCase(serviceRepository);
    }

    @Bean
    public AddServicePhotosUseCasePort addServicePhotosUseCase(ServiceRepositoryPort serviceRepository) {
        return new AddServicePhotosUseCase(serviceRepository);
    }

    @Bean
    public AddAttributeToServiceUseCasePort addAttributeToServiceUseCase(ServiceRepositoryPort serviceRepository) {
        return new AddAttributesToServiceUseCase(serviceRepository);
    }
}
