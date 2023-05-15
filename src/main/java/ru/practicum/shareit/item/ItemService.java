package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDtoWithBookingAndComments> getItems(Long userId);

    ItemForResponseDto getItem(Long itemId);

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, Long userId, Item item);

    List<ItemDto> searchItems(String text);

    void removeItem(Long itemId);

    ItemDtoWithBookingAndComments getItemWithBookingAndComment(Long itemId, Long ownerId);

    CommentDto saveComment(Long bookerId, Long itemId, CommentDto commentDto);
}
