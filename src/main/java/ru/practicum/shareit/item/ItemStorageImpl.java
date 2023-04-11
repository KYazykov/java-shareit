package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorageImp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {

    private final HashMap<Integer, Item> items = new HashMap<>();
    UserStorageImp userStorageImp = new UserStorageImp();
    private int id = 1;

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest() != null ? itemDto.getRequest() : null);
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest() != null ? item.getRequest() : null);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == userId) {
                itemDtoList.add(toItemDto(item));
            }
        }
        return itemDtoList;
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        return toItemDto(items.get(itemId));
    }

    @Override
    public Item addItem(Long userId, Item item) {
        User user = UserStorageImp.toUser(userStorageImp.getUser(userId));
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
        User user = UserStorageImp.toUser(userStorageImp.getUser(userId));
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
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> selectedItems = new ArrayList<>();
        if (text.isBlank()) {
            return List.of();
        }
        for (Item item : items.values()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()) {
                selectedItems.add(toItemDto(item));
            }
        }
        return selectedItems;
    }
}
