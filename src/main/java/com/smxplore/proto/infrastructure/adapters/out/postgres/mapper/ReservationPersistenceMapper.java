package com.smxplore.proto.infrastructure.adapters.out.postgres.mapper;

import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.ReservationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationPersistenceMapper {

    ReservationEntity toEntity(Reservation reservation);

    Reservation toDomain(ReservationEntity entity);
}
