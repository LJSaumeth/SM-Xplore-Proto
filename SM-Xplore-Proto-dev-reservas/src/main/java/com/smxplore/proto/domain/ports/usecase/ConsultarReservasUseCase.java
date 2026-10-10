package com.smxplore.proto.domain.ports.in;

import com.smxplore.proto.domain.model.Reserva;
import com.smxplore.proto.domain.model.ReservaStatus;

import java.util.List;

/** Consultar reservas / Consultar detalle / Ver historial. */
public interface ConsultarReservasUseCase {

    Reserva obtenerPorId(Long reservaId);

    List<Reserva> listarPorTurista(Integer turistaId);

    List<Reserva> listarPorEstado(ReservaStatus estado);
}