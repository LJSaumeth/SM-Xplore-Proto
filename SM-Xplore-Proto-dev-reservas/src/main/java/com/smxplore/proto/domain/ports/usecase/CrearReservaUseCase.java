package com.smxplore.proto.domain.ports.usecase;

import com.smxplore.proto.domain.model.Reserva;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Reservar servicio turístico / Reservar entrada para evento. */
public interface CrearReservaUseCase {

    Reserva crear(CrearReservaCommand command);

    record CrearReservaCommand(Integer turistaId,
                               int numberOfPeople,
                               LocalDateTime bookedFor,
                               BigDecimal total,
                               String notes,
                               String paymentMethod,
                               List<ItemCommand> items) {}

    /** Exactamente uno de los tres ids debe venir con valor. */
    record ItemCommand(Integer eventoId, Integer actividadId, Integer servicioId) {}
}