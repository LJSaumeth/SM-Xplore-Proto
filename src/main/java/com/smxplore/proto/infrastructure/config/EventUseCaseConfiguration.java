package com.smxplore.proto.infrastructure.config;

import com.smxplore.proto.application.events.ChangeEventStatusUseCase;
import com.smxplore.proto.application.events.ConsultEventsUseCase;
import com.smxplore.proto.application.events.EstimateEventAttendanceUseCase;
import com.smxplore.proto.application.events.FindEventsBySiteUseCase;
import com.smxplore.proto.application.events.FindEventsByZoneUseCase;
import com.smxplore.proto.application.events.RegisterEventUseCase;
import com.smxplore.proto.application.events.SearchEventsUseCase;
import com.smxplore.proto.application.events.ViewEventDetailsUseCase;
import com.smxplore.proto.domain.ports.repository.EventRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ISiteRepository;
import com.smxplore.proto.domain.ports.usecases.event.ChangeEventStatusUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.ConsultEventsUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.EstimateEventAttendanceUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.FindEventsBySiteUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.FindEventsByZoneUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.RegisterEventUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.SearchEventsUseCasePort;
import com.smxplore.proto.domain.ports.usecases.event.ViewEventDetailsUseCasePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class EventUseCaseConfiguration {

    @Bean
    public RegisterEventUseCasePort registerEventUseCase(EventRepositoryPort eventRepository) {
        return new RegisterEventUseCase(eventRepository);
    }

    @Bean
    public ViewEventDetailsUseCasePort viewEventDetailsUseCase(EventRepositoryPort eventRepository) {
        return new ViewEventDetailsUseCase(eventRepository);
    }

    @Bean
    public ConsultEventsUseCasePort consultEventsUseCase(EventRepositoryPort eventRepository) {
        return new ConsultEventsUseCase(eventRepository);
    }

    @Bean
    public SearchEventsUseCasePort searchEventsUseCase(EventRepositoryPort eventRepository) {
        return new SearchEventsUseCase(eventRepository);
    }

    @Bean
    public FindEventsBySiteUseCasePort findEventsBySiteUseCase(EventRepositoryPort eventRepository) {
        return new FindEventsBySiteUseCase(eventRepository);
    }

    @Bean
    public FindEventsByZoneUseCasePort findEventsByZoneUseCase(EventRepositoryPort eventRepository,
                                                               ISiteRepository siteRepository) {
        return new FindEventsByZoneUseCase(eventRepository, siteRepository);
    }

    @Bean
    public ChangeEventStatusUseCasePort changeEventStatusUseCase(EventRepositoryPort eventRepository) {
        return new ChangeEventStatusUseCase(eventRepository);
    }

    @Bean
    public EstimateEventAttendanceUseCasePort estimateEventAttendanceUseCase(EventRepositoryPort eventRepository) {
        return new EstimateEventAttendanceUseCase(eventRepository);
    }
}
