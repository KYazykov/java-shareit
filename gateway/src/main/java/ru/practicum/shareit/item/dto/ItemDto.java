package src.main.java.ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import src.main.java.ru.practicum.shareit.booking.Booking;
import src.main.java.ru.practicum.shareit.item.comment.Comment;
import src.main.java.ru.practicum.shareit.user.User;
import src.main.java.ru.practicum.shareit.validation.CreateObject;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Valid
public class ItemDto {
    private Long id;
    @NotBlank(message = "Поле логина не может быть пустым.")
    private String name;
    @NotBlank(message = "Поле описания не может быть пустым.")
    private String description;
    @NotNull(groups = {CreateObject.class}, message =
            "При создании новой записи о вещи необходимо указать её статус бронирования.")
    private Boolean available;
    private User owner;
    private Long requestId;
    private List<Booking> bookings;
    private List<Comment> comments;

}

