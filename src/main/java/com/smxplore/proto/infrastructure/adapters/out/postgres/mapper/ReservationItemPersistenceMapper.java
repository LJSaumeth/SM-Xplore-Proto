package com.smxplore.proto.infrastructure.adapters.out.postgres.mapper;

import com.smxplore.proto.domain.model.reservation.ReservationItem;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.ReservationItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationItemPersistenceMapper {

    ReservationItemEntity toEntity(ReservationItem reservationItem);

    ReservationItem toDomain(ReservationItemEntity entity);
}
