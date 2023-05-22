package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingForResponse add(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                  @RequestBody BookingDto bookingDto) {
        log.info("Создание брони.");
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingForResponse updateByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                            @PathVariable Long bookingId,
                                            @RequestParam Boolean approved) {
        log.info("Обновление брони владельцем. Подтверждение или отклонение брони.");
        return bookingService.updateBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingForResponse getWithStatusById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long bookingId) {
        log.info("Получение данных о конкретном бронировании (включая его статус).");
        return bookingService.getWithStatusById(userId, bookingId);
    }

    @GetMapping
    public List<BookingForResponse> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "state",
                                                        defaultValue = "ALL") String state,
                                                @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(name = "size", defaultValue = "20") @Positive Integer size) {
        log.info("Получение списка всех бронирований текущего пользователя.");
        return bookingService.getByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingForResponse> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                 @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(name = "size", defaultValue = "20") @Positive Integer size) {
        log.info("Получение списка бронирований для всех вещей текущего пользователя.");
        return bookingService.getByOwnerId(userId, state, from, size);
    }
}
