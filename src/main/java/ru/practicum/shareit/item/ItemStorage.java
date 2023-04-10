package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    List<Item> getItems(Long userId);

    Item getItem(Integer itemId);

    Item addItem(Long userId, Item item);

    Item updateItem(Integer itemId, Long userId, Item item);

    List<Item> searchItem(String text);
}
