package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    ItemDto itemDto = new ItemDto();

    @GetMapping
    public List<Item> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemDto.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public Item get(@PathVariable Integer itemId) {
        return itemDto.getItem(itemId);
    }

    @PostMapping
    public Item add(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody Item item) {
        return itemDto.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable int itemId,
                       @RequestBody Item item) {
        return itemDto.updateItem(itemId, userId, item);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam(value = "text") String text) {
        return itemDto.searchItem(text);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)// добавьте код ответа
    @ExceptionHandler
    public ErrorResponse handle(final ObjectNotFoundException e) {
        return new ErrorResponse(
                "Ошибка с пользователем", e.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)// добавьте код ответа
    @ExceptionHandler
    public ErrorResponse handle(final ItemNotFoundException e) {
        return new ErrorResponse(
                "Ошибка с предметом", e.getMessage()
        );
    }
}
