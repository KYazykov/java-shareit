package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */

@Repository
@Data
@RequiredArgsConstructor
@Slf4j
public class ItemDto implements ItemStorage {
    private String name;
    private String description;
    private boolean isAvailable;
    private Integer requestStatus;

    UserDto userDto = new UserDto();

    private int id = 1;
    private final HashMap<Integer, Item> items = new HashMap<>();

    public ItemDto(String name, String description, boolean isAvailable, Integer requestStatus) {
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.requestStatus = requestStatus;
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    @Override
    public List<Item> getItems(Long userId) {
        return new ArrayList<>(userDto.getUser(userId).getUserItems().values());
    }

    @Override
    public Item getItem(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public Item addItem(Long userId, Item item) {
        User user = userDto.getUser(userId);
        if (item.getAvailable() == null) {
            throw new ItemNotFoundException("Предмет должен быть доступен для аренды");
        }
        if (item.getName().isBlank()) {
            throw new ItemNotFoundException("Предмет должен иметь имя");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ItemNotFoundException("Предмет должен иметь описание");
        }
        item.setId(id++);
        item.setOwner(userId);
        items.put(item.getId(), item);
        user.getUserItems().put(item.getId(), item);
        log.info("Предмет добавлен: {}", item);
        return item;
    }

    @Override
    public Item updateItem(Integer itemId, Long userId, Item item) {
        User user = userDto.getUser(userId);
        if (!items.containsKey(itemId)) {
            throw new ObjectNotFoundException("Такого предмета нет");
        }
        if (!Objects.equals(items.get(itemId).getOwner(), userId)) {
            throw new ObjectNotFoundException("Вещь принадлежит другому человеку");
        }
        if (!Objects.equals(userId, items.get(itemId).getOwner())
                || item.getRequest() != items.get(itemId).getRequest()
                || itemId != items.get(itemId).getId()) {
            throw new ObjectNotFoundException("Нельзя менять владельца, айди или реквест вещи");
        }
        if (item.getName() == null) {
            item.setName(items.get(itemId).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(itemId).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(itemId).getAvailable());
        }
        items.get(itemId);
        item.setId(itemId);
        item.setOwner(userId);
        items.put(itemId, item);
        user.getUserItems().put(itemId, item);
        log.info("Предмет обновлен: {}", item);
        return item;
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> selectedItems = new ArrayList<>();
        if (text.isBlank()) {
            return List.of();
        }
        for (Item item : items.values()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }
}

