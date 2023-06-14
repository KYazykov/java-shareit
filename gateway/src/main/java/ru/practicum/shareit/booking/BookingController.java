package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                      @Validated(CreateObject.class) @RequestBody BookingDto bookingDto) {
        log.info("Создание бронирования.");
        checkStartAndEndTimes(bookingDto);
        return bookingClient.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("Обновление брони владельцем. Подтверждение или отклонение брони.");
        return bookingClient.updateByOwner(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getWithStatusById(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @NotNull @PathVariable Long bookingId) {
        log.info("Получение данных о конкретном бронировании (включая его статус).");
        return bookingClient.getWithStatusById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(value = "state",
                                                      defaultValue = "ALL") String state,
                                              @Min(0) @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Min(1) @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получение списка всех бронирований текущего пользователя.");
        BookingState bookingState = BookingState.parse(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        return bookingClient.getByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(value = "state", defaultValue = "ALL") String state,
                                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получение списка бронирований для всех вещей текущего пользователя.");
        BookingState bookingState = BookingState.parse(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        return bookingClient.getByOwnerId(userId, state, from, size);
    }

    private void checkStartAndEndTimes(BookingDto bookingDto) {
        if (bookingDto.getStartTime().isAfter(bookingDto.getEndTime()) ||
                bookingDto.getStartTime().equals(bookingDto.getEndTime())) {
            String message = "Окончание бронирования не может быть раньше его начала или равняться ему";
            log.info(message);
            throw new ValidateException(message);
        }
    }
}
