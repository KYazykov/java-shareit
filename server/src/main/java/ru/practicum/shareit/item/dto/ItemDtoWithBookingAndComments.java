package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDtoWithBookingAndComments {
    private Long id;
    private String name;
    private String description;
    private Boolean available = null;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private Long requestId;
    private List<CommentDto> comments;
}
