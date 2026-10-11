package com.smxplore.proto.domain.model.reservation;

import com.smxplore.proto.domain.exceptions.reservation.InvalidBookingDateException;
import com.smxplore.proto.domain.exceptions.reservation.InvalidNumberOfPeopleException;
import com.smxplore.proto.domain.exceptions.reservation.InvalidPaymentMethodException;
import com.smxplore.proto.domain.exceptions.reservation.InvalidReservationItemException;
import com.smxplore.proto.domain.exceptions.reservation.InvalidReservationStatusTransitionException;
import com.smxplore.proto.domain.exceptions.reservation.InvalidReservationTotalException;
import com.smxplore.proto.domain.exceptions.reservation.InvalidReservationTouristException;
import com.smxplore.proto.domain.exceptions.reservation.ReservationWithoutItemsException;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
@Getter
public class Reservation {
    private static final int MAX_PAYMENT_METHOD_LENGTH = 50;

    private final UUID id;
    private final UUID touristId;
    private final BigDecimal total;
    private final int numberOfPeople;
    private final Instant bookedFor;
    private final String notes;
    private final String paymentMethod;

    @Builder.Default
    private ReservationStatus status = ReservationStatus.PENDING;

    @Builder.Default
    private final Instant bookedAt = Instant.now();

    @Builder.Default
    private final List<ReservationItem> items = new ArrayList<>();

    public void validate() {
        validateTourist();
        validateNumberOfPeople();
        validateTotal();
        validateBookingDate();
        validatePaymentMethod();
        validateItems();
    }

    public void confirm() {
        changeStatus(ReservationStatus.CONFIRMED);
    }

    public void reject() {
        changeStatus(ReservationStatus.REJECTED);
    }

    public void requestCancellation() {
        changeStatus(ReservationStatus.CANCELLATION_REQUESTED);
    }

    public void cancel() {
        changeStatus(ReservationStatus.CANCELLED);
    }

    public void registerNoShow() {
        changeStatus(ReservationStatus.NO_SHOW);
    }

    public void registerAttendance() {
        changeStatus(ReservationStatus.COMPLETED);
    }

    public List<ReservationItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    private void changeStatus(ReservationStatus target) {
        if (!status.canTransitionTo(target))
            throw new InvalidReservationStatusTransitionException(
                    "Cannot change reservation from %s to %s".formatted(status, target));
        status = target;
    }

    private void validateTourist() {
        if (touristId == null)
            throw new InvalidReservationTouristException("Tourist is mandatory.");
    }

    private void validateNumberOfPeople() {
        if (numberOfPeople <= 0)
            throw new InvalidNumberOfPeopleException("Number of people must be greater than zero.");
    }

    private void validateTotal() {
        if (total == null || total.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidReservationTotalException("Total must not be null or negative.");
    }

    private void validateBookingDate() {
        if (bookedFor == null || bookedAt == null || !bookedFor.isAfter(bookedAt))
            throw new InvalidBookingDateException("Booking date must be in the future.");
    }

    private void validatePaymentMethod() {
        if (paymentMethod != null && paymentMethod.length() > MAX_PAYMENT_METHOD_LENGTH)
            throw new InvalidPaymentMethodException(
                    "Payment method must not exceed %d characters.".formatted(MAX_PAYMENT_METHOD_LENGTH));
    }

    private void validateItems() {
        if (items == null || items.isEmpty())
            throw new ReservationWithoutItemsException("Reservation must have at least one item.");
        items.forEach(item -> {
            if (item == null)
                throw new InvalidReservationItemException("Item must not be null.");
            item.validate();
        });
    }
}
