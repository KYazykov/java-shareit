package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    UserStorage userStorage;
    private final UserService userService = new UserService(userStorage);
    private final ItemStorageImpl itemStorageImp = new ItemStorageImpl(userService);

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemStorageImp.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Integer itemId) {
        return itemStorageImp.getItem(itemId);
    }

    @PostMapping
    public ItemDto add(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody ItemDto itemDto) {
        return itemStorageImp.addItem(userId, ItemMapper.toItem(itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable int itemId,
                          @RequestBody ItemDto itemDto) {
        return itemStorageImp.updateItem(itemId, userId, ItemMapper.toItem(itemDto));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(value = "text") String text) {
        return itemStorageImp.searchItem(text);
    }

}
