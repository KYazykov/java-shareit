package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepositoryJpa;
import ru.practicum.shareit.user.UserMapper;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemRepositoryJpa itemRepositoryJpa;

    public Booking toBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                itemRepositoryJpa.getReferenceById(bookingDto.getItemId()),
                bookingDto.getBooker(),
                bookingDto.getStartTime(),
                bookingDto.getEndTime(),
                bookingDto.getBookingStatus());
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                booking.getBooker(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getBookingStatus());
    }

    public BookingForResponse toBookingForResponse(Booking booking) {
        return new BookingForResponse(
                booking.getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                ItemMapper.toItemForResponseDto(booking.getItem()),
                UserMapper.toUserOnlyWithIdDto(booking.getBooker()),
                booking.getBookingStatus());
    }

    public BookingForItemDto toBookingForItemDto(Booking booking) {
        return new BookingForItemDto(
                booking.getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getBooker().getId(),
                booking.getBookingStatus());
    }
}
