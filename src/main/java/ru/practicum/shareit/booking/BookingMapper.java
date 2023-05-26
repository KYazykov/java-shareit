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
                item.getOwner(),
                bookingDto.getStartTime(),
                bookingDto.getEndTime(),
                bookingDto.getBookingStatus());
    }

    public static Booking toBooking(BookingForResponse bookingForResponse, Item item) {
        return new Booking(
                bookingForResponse.getId(),
                item,
                item.getOwner(),
                bookingForResponse.getStartTime(),
                bookingForResponse.getEndTime(),
                bookingForResponse.getStatus());
    }

    public static Booking toBooking(BookingForItemDto bookingForItemDto, Item item) {
        return new Booking(
                bookingForItemDto.getId(),
                item,
                item.getOwner(),
                bookingForItemDto.getStartTime(),
                bookingForItemDto.getEndTime(),
                bookingForItemDto.getStatus());
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                UserMapper.toUserForResponse(booking.getBooker()),
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
