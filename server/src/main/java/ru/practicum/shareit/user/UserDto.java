package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private Long id;
    private List<Item> userItems;
    private String email;
    private String name;
    private List<Booking> bookings;
    private List<Comment> comments;

}
