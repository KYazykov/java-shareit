package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoWithBookingAndComments> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingAndComments getItemByIdForOwner(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                                             @PathVariable Long itemId) {
        return itemService.getItemWithBookingAndComment(itemId, ownerId);
    }

    @PostMapping
    public ItemForResponseDto add(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody ItemDto itemDto) {
        Item itemNew = ItemMapper.toItem(itemService.addItem(userId, itemDto));
        return ItemMapper.toItemForResponseDto(itemNew);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, userId, ItemMapper.toItem(itemDto));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(value = "text", required = false) String text,
                                @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long itemId, @RequestBody CommentDto inputCommentDto) {
        return itemService.saveComment(userId, itemId, inputCommentDto);
    }

}
