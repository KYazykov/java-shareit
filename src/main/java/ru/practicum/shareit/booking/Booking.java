package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    int id;
    LocalDate start;
    LocalDate end;
    int item;
    int booker;
    Status bookingStatus;
    int minutesRent;
    boolean rentConfirmed;
}
