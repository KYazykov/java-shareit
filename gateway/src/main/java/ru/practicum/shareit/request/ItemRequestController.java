package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long requesterId,
                                                 @RequestBody @Validated(CreateObject.class) ItemRequestDto itemRequestDto) {
        log.info("Добавление нового запроса вещи в БД. Запрос = {}", itemRequestDto);
        return itemRequestClient.addItemRequest(requesterId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserId(
            @NotNull @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Получение списка запросов пользователя с ID = '{}'.", requesterId);
        return itemRequestClient.getItemRequestsByUserId(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @NotNull @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @Min(0) @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Min(1) @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получение списка запросов, созданных другими пользователями кроме ID = '{}'.", requesterId);
        return itemRequestClient.getAllRequests(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequestById(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @NotNull @PathVariable Long requestId) {
        log.info("Получение запроса на вещь с определённым ID.");
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
