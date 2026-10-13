package com.smxplore.proto.infrastructure.transaction;

import com.smxplore.proto.domain.model.reservation.ReservationItem;
import com.smxplore.proto.domain.ports.usecases.reservation.AddReservationItemsUseCasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.UUID;

/**
 * Envuelve el caso de uso en una transacción de R2DBC: el guardado de los
 * items y la actualización del total de la reserva ocurren de forma atómica
 * (si algo falla, no queda nada a medias). Además los items solo se emiten
 * después de que el commit fue exitoso.
 * <p>
 * Se implementa como decorador en infraestructura para que el caso de uso
 * (capa de aplicación) siga sin depender de Spring.
 */
@RequiredArgsConstructor
public class TransactionalAddReservationItemsUseCase implements AddReservationItemsUseCasePort {

    private final AddReservationItemsUseCasePort delegate;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Flux<ReservationItem> handle(UUID reservationId, Collection<ReservationItem> items) {
        // collectList() dentro de la transacción: los items se emiten recién
        // cuando el commit fue exitoso (el cliente nunca ve resultados a medias)
        return transactionalOperator.transactional(delegate.handle(reservationId, items).collectList())
                .flatMapMany(Flux::fromIterable);
    }
}
