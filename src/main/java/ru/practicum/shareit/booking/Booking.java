package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Booking {
    private int id;
    private LocalDate start;
    private LocalDate end;
    private int item;
    private int booker;
    private Status bookingStatus;
    private int minutesRent;
    private boolean rentConfirmed;
}
