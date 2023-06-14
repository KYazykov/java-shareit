package ru.practicum.shareit.booking;


import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;

import java.util.List;

public interface BookingService {

    BookingForResponse createBooking(Long bookerId, BookingDto bookingDto);


    BookingForResponse updateBooking(Long ownerId, Long bookingId, Boolean approved);


    BookingForResponse getWithStatusById(Long userId, Long bookingId);


    List<BookingForResponse> getByUserId(Long userId, String state, Integer from, Integer size);


    List<BookingForResponse> getByOwnerId(Long userId, String state, Integer from, Integer size);
}