package com.smxplore.proto.application.reservations;

import com.smxplore.proto.domain.exceptions.activity.ActivityNotFoundException;
import com.smxplore.proto.domain.exceptions.reservation.DifferentProvidersInReservationException;
import com.smxplore.proto.domain.exceptions.reservation.InvalidReservationItemException;
import com.smxplore.proto.domain.exceptions.reservation.MixedReservationItemTypesException;
import com.smxplore.proto.domain.exceptions.reservation.ReservationNotFoundException;
import com.smxplore.proto.domain.exceptions.reservation.TooManyReservationItemsException;
import com.smxplore.proto.domain.exceptions.service.InvalidServiceProviderException;
import com.smxplore.proto.domain.exceptions.service.ServiceNotFoundException;
import com.smxplore.proto.domain.model.activity.Activity;
import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.model.reservation.ReservationItem;
import com.smxplore.proto.domain.model.reservation.ReservationItemType;
import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ReservationItemRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ReservationRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.reservation.AddReservationItemsUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Reglas de negocio de los items de una reserva:
 * <ul>
 *     <li>La reserva debe existir y no estar en un estado final.</li>
 *     <li>Todos los items de una reserva deben ser del mismo tipo y hay un
 *     máximo de {@value #MAX_ITEMS_PER_RESERVATION} items por reserva.</li>
 *     <li>Los items de tipo SERVICE deben pertenecer a servicios ofrecidos por un mismo provider
 *     y su precio se toma del catálogo (el enviado por el cliente se ignora).</li>
 *     <li>Los items de tipo ACTIVITY deben pertenecer a actividades del mismo guía y su precio
 *     también se toma del catálogo.</li>
 *     <li>Los items de tipo EVENT están limitados a uno por reserva.</li>
 *     <li>La reserva recuerda su provider (items SERVICE) o su guía (items ACTIVITY) para que
 *     cada prestadora o guía pueda consultar las reservas que recibió.</li>
 * </ul>
 */
@RequiredArgsConstructor
public class AddReservationItemsUseCase implements AddReservationItemsUseCasePort {

    static final int MAX_ITEMS_PER_RESERVATION = 100;

    private final ReservationRepositoryPort reservationRepo;
    private final ReservationItemRepositoryPort reservationItemRepo;
    private final ServiceRepositoryPort serviceRepo;
    private final ActivityRepositoryPort activityRepo;

    @Override
    public Flux<ReservationItem> handle(UUID reservationId, Collection<ReservationItem> items) {
        return Mono.fromCallable(() -> prepareItems(reservationId, items))
                .flatMapMany(prepared -> reservationRepo.findByIdForUpdate(reservationId)
                        .switchIfEmpty(Mono.error(new ReservationNotFoundException(reservationId)))
                        .flatMapMany(reservation -> {
                            reservation.validateItemsCanBeAdded();
                            return reservationItemRepo.findByReservationId(reservationId)
                                    .collectList()
                                    .flatMapMany(existing -> resolveItems(existing, prepared)
                                            .flatMapMany(resolved -> Flux.defer(() -> reservationItemRepo.saveAll(resolved))
                                                    .collectList()
                                                    .flatMap(saved -> assignOwner(reservation, resolved)
                                                            .then(recalculateTotal(reservation, existing, saved))
                                                            .thenReturn(saved))
                                                    .flatMapMany(Flux::fromIterable)));
                        }));
    }

    private List<ReservationItem> prepareItems(UUID reservationId, Collection<ReservationItem> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidReservationItemException("At least one reservation item is required.");
        }
        var prepared = items.stream()
                .map(item -> prepareItem(reservationId, item))
                .toList();
        if (prepared.stream().map(ReservationItem::getType).distinct().count() > 1) {
            throw new MixedReservationItemTypesException("All the items of a reservation must have the same type.");
        }
        return prepared;
    }

    private ReservationItem prepareItem(UUID reservationId, ReservationItem item) {
        if (item == null) {
            throw new InvalidReservationItemException("Reservation item cannot be null.");
        }
        if (item.getItemId() == null) {
            throw new InvalidReservationItemException("Item id is mandatory.");
        }
        if (item.getType() == null) {
            throw new InvalidReservationItemException("Item type is mandatory.");
        }
        return item.toBuilder().reservationId(reservationId).build();
    }

    /**
     * Valida las reglas y resuelve el precio de los items entrantes: para los
     * items SERVICE y ACTIVITY el precio es el del catálogo; EVENT usa el
     * precio recibido mientras no exista su catálogo.
     */
    private Mono<List<ReservationItem>> resolveItems(List<ReservationItem> existing, List<ReservationItem> incoming) {
        var incomingType = incoming.getFirst().getType();
        if (!existing.isEmpty() && existing.getFirst().getType() != incomingType) {
            return Mono.error(new MixedReservationItemTypesException(
                    "The reservation already has %s items, it cannot accept %s items."
                            .formatted(existing.getFirst().getType(), incomingType)));
        }
        if (existing.size() + incoming.size() > MAX_ITEMS_PER_RESERVATION) {
            return Mono.error(new TooManyReservationItemsException(
                    "A reservation cannot have more than %d items.".formatted(MAX_ITEMS_PER_RESERVATION)));
        }
        if (incomingType == ReservationItemType.EVENT && existing.size() + incoming.size() > 1) {
            return Mono.error(new TooManyReservationItemsException("An EVENT reservation can only have one item."));
        }
        return switch (incomingType) {
            case SERVICE -> resolveServices(existing, incoming);
            case ACTIVITY -> resolveActivities(existing, incoming);
            case EVENT -> Mono.just(validate(incoming));
        };
    }

    private Mono<List<ReservationItem>> resolveServices(List<ReservationItem> existing, List<ReservationItem> incoming) {
        var serviceIds = Stream.concat(existing.stream(), incoming.stream())
                .map(ReservationItem::getItemId)
                .distinct()
                .toList();
        return Flux.fromIterable(serviceIds)
                .concatMap(serviceId -> serviceRepo.findById(serviceId)
                        .switchIfEmpty(Mono.error(new ServiceNotFoundException(serviceId))))
                .map(service -> {
                    if (service.getProvider() == null) {
                        throw new InvalidServiceProviderException(
                                "Service %s has no provider.".formatted(service.getId()));
                    }
                    return service;
                })
                .collectMap(Service::getId)
                .flatMap(services -> {
                    var providers = services.values().stream()
                            .map(service -> service.getProvider().id())
                            .distinct()
                            .count();
                    if (providers > 1) {
                        return Mono.error(new DifferentProvidersInReservationException(
                                "All the services of a reservation must be offered by the same provider."));
                    }
                    var resolved = incoming.stream()
                            .map(item -> item.toBuilder().price(services.get(item.getItemId()).getPrice()).build())
                            .toList();
                    return Mono.just(validate(resolved));
                });
    }

    private Mono<List<ReservationItem>> resolveActivities(List<ReservationItem> existing, List<ReservationItem> incoming) {
        var activityIds = Stream.concat(existing.stream(), incoming.stream())
                .map(ReservationItem::getItemId)
                .distinct()
                .toList();
        return Flux.fromIterable(activityIds)
                .concatMap(activityId -> activityRepo.findById(activityId)
                        .switchIfEmpty(Mono.error(new ActivityNotFoundException(activityId))))
                .collectMap(Activity::getId)
                .flatMap(activities -> {
                    var guides = activities.values().stream()
                            .map(Activity::getGuideId)
                            .filter(Objects::nonNull)
                            .distinct()
                            .count();
                    if (guides > 1) {
                        return Mono.error(new DifferentProvidersInReservationException(
                                "All the activities of a reservation must be offered by the same guide."));
                    }
                    var resolved = incoming.stream()
                            .map(item -> item.toBuilder().price(activities.get(item.getItemId()).getPrice()).build())
                            .toList();
                    return Mono.just(validate(resolved));
                });
    }

    /** Fixes the provider (SERVICE items) or the guide (ACTIVITY items) of the reservation. */
    private Mono<Void> assignOwner(Reservation reservation, List<ReservationItem> items) {
        return switch (items.getFirst().getType()) {
            case SERVICE -> Flux.fromIterable(items)
                    .map(ReservationItem::getItemId)
                    .distinct()
                    .concatMap(serviceRepo::findById)
                    .mapNotNull(Service::getProvider)
                    .map(provider -> provider.id())
                    .distinct()
                    .singleOrEmpty()
                    .doOnNext(reservation::assignProvider)
                    .then();
            case ACTIVITY -> Flux.fromIterable(items)
                    .map(ReservationItem::getItemId)
                    .distinct()
                    .concatMap(activityRepo::findById)
                    .mapNotNull(Activity::getGuideId)
                    .distinct()
                    .singleOrEmpty()
                    .doOnNext(reservation::assignGuide)
                    .then();
            case EVENT -> Mono.empty();
        };
    }

    private List<ReservationItem> validate(List<ReservationItem> items) {
        items.forEach(ReservationItem::validate);
        return items;
    }

    /**
     * El total de la reserva es la suma de los precios de todos sus items.
     */
    private Mono<Void> recalculateTotal(Reservation reservation, List<ReservationItem> existing,
                                        List<ReservationItem> saved) {
        var total = Stream.concat(existing.stream(), saved.stream())
                .map(ReservationItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        reservation.changePrice(total);
        return reservationRepo.save(reservation).then();
    }
}
