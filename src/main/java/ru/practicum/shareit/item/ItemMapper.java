package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequestId(),
                itemDto.getBookings(),
                itemDto.getComments());
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequestId(),
                item.getBookings(),
                item.getComments());
    }

    public static ItemDtoWithBookingAndComments toItemDtoWithBookingAndComments(Item item) {
        return new ItemDtoWithBookingAndComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                (BookingForItemDto) null,
                (BookingForItemDto) null,
                item.getRequestId(),
                item.getComments().stream().map(o ->
                        new CommentDto(o.getId(), o.getText(), o.getItem().getId(), o.getAuthor().getName(),
                                o.getCreated())).collect(Collectors.toList()));
    }

    public static ItemForResponseDto toItemForResponseDto(Item item) {
        return new ItemForResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId());
    }
}
