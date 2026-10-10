package com.smxplore.proto.domain.ports.usecase;

import com.smxplore.proto.domain.model.Reserva;
import com.smxplore.proto.domain.model.ReservaStatus;


public interface ActualizarEstadoReservaUseCase {

    Reserva actualizarEstado(Long reservaId, ReservaStatus nuevoEstado);
}