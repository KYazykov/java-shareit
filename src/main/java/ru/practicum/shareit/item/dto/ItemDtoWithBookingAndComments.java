package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDtoWithBookingAndComments {
    private Long id;
    @NotBlank(message = "Поле логина не может быть пустым.")
    private String name;
    @NotBlank(message = "Поле описания не может быть пустым.")
    private String description;
    private Boolean available = null;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private Long requestId;
    private List<CommentDto> comments;
}
