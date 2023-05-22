package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long requesterId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Добавление нового запроса вещи в БД. Запрос = {}", itemRequestDto);
        return itemRequestService.addItemRequest(requesterId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoWithAnswers> getItemRequestsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Получение списка запросов пользователя с ID = '{}'.", requesterId);
        return itemRequestService.getItemRequestsByUserId(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithAnswers> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "20") @Positive Integer size) {
        log.info("Получение списка запросов, созданных другими пользователями кроме ID = '{}'.", requesterId);
        return itemRequestService.getAllRequestForSee(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDtoWithAnswers getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @PathVariable Long requestId) {
        log.info("Получение запроса на вещь с определённым ID.");
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
