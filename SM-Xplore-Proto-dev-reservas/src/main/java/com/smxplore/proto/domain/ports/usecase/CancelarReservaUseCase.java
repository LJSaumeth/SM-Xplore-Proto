package com.smxplore.proto.domain.ports.usecase;

import com.smxplore.proto.domain.model.Reserva;

/** Cancelar reserva (turista / guía) y gestionar cancelación solicitada. */
public interface CancelarReservaUseCase {

    /** El turista pide cancelar una reserva ya confirmada. */
    Reserva solicitarCancelacion(Long reservaId);

    /** Cancelación efectiva. */
    Reserva cancelar(Long reservaId);
}