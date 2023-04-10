package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

public interface ItemStorage {

    List<Item> getItems(Long userId);
    Item getItem(Integer itemId);
    Item addItem(Long userId, Item item);
    Item updateItem(Integer itemId, Long userId, Item item);
    List<Item> searchItem(String text);
}
