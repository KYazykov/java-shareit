package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;
    @NotBlank(message = "Поле логина не может быть пустым.")
    private String name;
    @NotBlank(message = "Поле описания не может быть пустым.")
    private String description;
    private Boolean available = null;
    private User owner;
    private Long requestId;
    private List<Booking> bookings;
    private List<Comment> comments;

}

