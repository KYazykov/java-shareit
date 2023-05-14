package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    ItemStorageImpl itemStorageImp = new ItemStorageImpl();

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemStorageImp.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Integer itemId) {
        return itemStorageImp.getItem(itemId);
    }

    @PostMapping
    public Item add(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody ItemDto itemDto) {
        return itemStorageImp.addItem(userId, ItemStorageImpl.toItem(itemDto));
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable int itemId,
                       @RequestBody ItemDto itemDto) {
        return itemStorageImp.updateItem(itemId, userId, ItemStorageImpl.toItem(itemDto));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(value = "text") String text) {
        return itemStorageImp.searchItem(text);
    }

}
