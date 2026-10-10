package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.Reserva;
import com.smxplore.proto.domain.model.ReservaStatus;

import java.util.List;
import java.util.Optional;

public interface ReservaRepositoryPort {

    Reserva guardar(Reserva reserva);

    Optional<Reserva> buscarPorId(Long id);

    List<Reserva> buscarPorTurista(Integer turistaId);

    List<Reserva> buscarPorEstado(ReservaStatus estado);
}