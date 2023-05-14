package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    List<ItemDto> getItems(Long userId);

    ItemDto getItem(Integer itemId);

    Item addItem(Long userId, Item item);

    Item updateItem(Integer itemId, Long userId, Item item);

    List<ItemDto> searchItem(String text);
}
