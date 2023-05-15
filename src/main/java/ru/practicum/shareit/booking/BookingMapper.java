package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, Item item) {
        return new Booking(
                bookingDto.getId(),
                item,
                bookingDto.getBooker(),
                bookingDto.getStartTime(),
                bookingDto.getEndTime(),
                bookingDto.getBookingStatus());
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                booking.getBooker(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getBookingStatus());
    }

    public static BookingForResponse toBookingForResponse(Booking booking) {
        return new BookingForResponse(
                booking.getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                ItemMapper.toItemForResponseDto(booking.getItem()),
                UserMapper.toUserOnlyWithIdDto(booking.getBooker()),
                booking.getBookingStatus());
    }

    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        return new BookingForItemDto(
                booking.getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getBooker().getId(),
                booking.getBookingStatus());
    }
}
