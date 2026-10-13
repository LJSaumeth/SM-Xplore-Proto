package com.smxplore.proto.infrastructure.config;

import com.smxplore.proto.application.reservations.AddReservationItemUseCase;
import com.smxplore.proto.application.reservations.AddReservationItemsUseCase;
import com.smxplore.proto.application.reservations.ConsultOneReservationItemUseCase;
import com.smxplore.proto.application.reservations.ConsultReservationItemsByTypeUseCase;
import com.smxplore.proto.application.reservations.ConsultReservationItemsUseCase;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ReservationItemRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ReservationRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.reservation.AddReservationItemUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.AddReservationItemsUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultOneReservationItemUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationItemsByTypeUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationItemsUseCasePort;
import com.smxplore.proto.infrastructure.transaction.TransactionalAddReservationItemsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration(proxyBeanMethods = false)
public class ReservationItemUseCaseConfiguration {

    @Bean
    public AddReservationItemsUseCasePort addReservationItemsUseCase(ReservationRepositoryPort reservationRepository,
                                                                     ReservationItemRepositoryPort reservationItemRepository,
                                                                     ServiceRepositoryPort serviceRepository,
                                                                     ActivityRepositoryPort activityRepository,
                                                                     TransactionalOperator transactionalOperator) {
        var useCase = new AddReservationItemsUseCase(
                reservationRepository, reservationItemRepository, serviceRepository, activityRepository);
        return new TransactionalAddReservationItemsUseCase(useCase, transactionalOperator);
    }

    @Bean
    public AddReservationItemUseCasePort addReservationItemUseCase(
            AddReservationItemsUseCasePort addReservationItemsUseCase) {
        return new AddReservationItemUseCase(addReservationItemsUseCase);
    }

    @Bean
    public ConsultReservationItemsUseCasePort consultReservationItemsUseCase(
            ReservationRepositoryPort reservationRepository,
            ReservationItemRepositoryPort reservationItemRepository) {
        return new ConsultReservationItemsUseCase(reservationRepository, reservationItemRepository);
    }

    @Bean
    public ConsultOneReservationItemUseCasePort consultOneReservationItemUseCase(
            ReservationItemRepositoryPort reservationItemRepository) {
        return new ConsultOneReservationItemUseCase(reservationItemRepository);
    }

    @Bean
    public ConsultReservationItemsByTypeUseCasePort consultReservationItemsByTypeUseCase(
            ReservationItemRepositoryPort reservationItemRepository) {
        return new ConsultReservationItemsByTypeUseCase(reservationItemRepository);
    }
}
