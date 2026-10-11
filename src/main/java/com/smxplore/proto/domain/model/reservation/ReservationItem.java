package com.smxplore.proto.domain.model.reservation;

import com.smxplore.proto.domain.exceptions.reservation.InvalidReservationItemException;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder(toBuilder = true)
@Getter
public class ReservationItem {
    private final UUID id;
    private final UUID reservationId;
    private final UUID eventId;
    private final UUID activityId;
    private final UUID serviceId;

    public void validate() {
        int references = (eventId != null ? 1 : 0)
                + (activityId != null ? 1 : 0)
                + (serviceId != null ? 1 : 0);
        if (references != 1)
            throw new InvalidReservationItemException(
                    "Item must reference exactly one event, activity or service.");
    }

    public ReservationItemType type() {
        if (eventId != null) return ReservationItemType.EVENT;
        if (activityId != null) return ReservationItemType.ACTIVITY;
        return ReservationItemType.SERVICE;
    }
}
