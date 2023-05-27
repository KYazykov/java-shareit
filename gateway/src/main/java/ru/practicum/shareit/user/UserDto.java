package src.main.java.ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import src.main.java.ru.practicum.shareit.booking.Booking;
import src.main.java.ru.practicum.shareit.item.comment.Comment;
import src.main.java.ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private Long id;
    private List<Item> userItems;
    @Email
    @NotBlank(message = "Поле имени не может быть пустым.")
    private String email;
    @NotBlank(message = "Поле имени не может быть пустым.")
    private String name;
    private List<Booking> bookings;
    private List<Comment> comments;

}