package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.exception.BookingValidateException;
import ru.practicum.shareit.exception.ItemAvailableException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.ItemRepositoryJpa;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepositoryJpa bookingRepositoryJpa;
    private final ItemRepositoryJpa itemRepositoryJpa;
    private final UserRepositoryJpa userRepositoryJpa;

    @Transactional
    @Override
    public BookingForResponse createBooking(Long bookerId, BookingDto bookingDto) {
        if (bookingDto.getItemId() == null) {
            throw new ObjectNotFoundException("При создании бронирования не найдена вещь с данным ID");
        }
        Item itemFromDB = itemRepositoryJpa.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ObjectNotFoundException("При создании бронирования не найдена вещь с данным ID в БД."));
        if (!itemFromDB.getAvailable()) {
            throw new ItemAvailableException("Вещь нельзя забронировать, поскольку available = false.");
        }
        User bookerFromDb = userRepositoryJpa.findById(bookerId)
                .orElseThrow(() -> new ObjectNotFoundException("При создании бронирования не найден пользователь " +
                        "с ID = " + bookerId + " в БД."));

        validateBooking(bookingDto, itemFromDB, bookerFromDb);
        bookingDto.setBookingStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.toBooking(bookingDto, itemFromDB);
        booking.setItem(itemFromDB);
        booking.setBooker(bookerFromDb);
        Booking result = bookingRepositoryJpa.save(booking);
        return BookingMapper.toBookingForResponse(result);
    }
    @Override
    @Transactional
    public BookingForResponse updateBooking(Long ownerId, Long bookingId, Boolean approved) {
        Booking bookingFromBd = bookingRepositoryJpa.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException(
                "При обновлении бронирования не найдено бронирование с ID = '" + bookingId + "' в БД."));
        if (Objects.equals(BookingStatus.APPROVED, bookingFromBd.getBookingStatus()) && approved) {
            String message = "Данное бронирование уже было обработано и имеет статус '"
                    + bookingFromBd.getBookingStatus() + "'.";
            log.info(message);
            throw new BookingValidateException(message);
        }
        User ownerFromDb = userRepositoryJpa.findById(ownerId).orElseThrow(() -> new ObjectNotFoundException("При " +
                "обновлении бронирования не найден пользователь с ID = '" + ownerId + "' в БД."));
        List<Item> items = new ArrayList<>(ownerFromDb.getUserItems());
        for (Item i : items) {
            Long itemIdFromBookingBd = bookingFromBd.getItem().getId();
            if (i.getId().equals(itemIdFromBookingBd)) {
                bookingFromBd.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
                Booking result = bookingRepositoryJpa.save(bookingFromBd);
                return BookingMapper.toBookingForResponse(result);
            }
        }
        String message = "При обновлении брони у хозяина вещи эта вещь не найдена. Ошибка в запросе.";
        log.info(message);
        throw new ObjectNotFoundException(message);
    }
    @Override
    public BookingForResponse getWithStatusById(Long userId, Long bookingId) {
        Booking booking = bookingRepositoryJpa.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Бронирование с ID = '" + bookingId
                        + " не найдено в БД при его получении."));
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();
        if (userId.equals(bookerId) || userId.equals(ownerId)) {
            return BookingMapper.toBookingForResponse(booking);
        }
        throw new ObjectNotFoundException("Ошибка при получении брони с ID = '" + bookingId
                + "'. Пользователь с ID = '" + userId
                + "' не является ни хозяином, ни пользователем, забронировавшим вещь.");
    }

    @Override
    public List<BookingForResponse> getByUserId(Long userId, String state, Integer from, Integer size) {
        final LocalDateTime nowDateTime = LocalDateTime.now();
        if (from < 0) {
            throw new BookingValidateException("Отрицательный параметр пагинации from = '" + from + "'.");
        }
        if (size < 1) {
            throw new BookingValidateException("Не верный параметр пагинации size = '" + size + "'.");
        }
        BookingState bookingState;

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("startTime"));

        if (state.isBlank()) {
            bookingState = BookingState.ALL;
        } else {
            try {
                bookingState = BookingState.valueOf(state);
            } catch (IllegalArgumentException ex) {
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
        User bookerFromDb = userRepositoryJpa.findById(userId).orElseThrow(() -> new ObjectNotFoundException("При " +
                "получении списка бронирований не найден пользователь (арендующий) с ID = " + userId + " в БД."));
        List<Booking> result = new ArrayList<>();

        switch (bookingState) {
            case ALL: {
                result = bookingRepositoryJpa.findAllByBookerOrderByStartTimeDesc(bookerFromDb, pageable);
                break;
            }
            case CURRENT: {
                result = bookingRepositoryJpa.findAllBookingsForBookerWithStartAndEndTime(
                        bookerFromDb, nowDateTime, nowDateTime, pageable);
                break;
            }
            case PAST: {
                result = bookingRepositoryJpa.findAllByBookerAndEndTimeIsBeforeOrderByStartTimeDesc(
                        bookerFromDb, nowDateTime, pageable);
                break;
            }
            case FUTURE: {
                result = bookingRepositoryJpa.findAllByBookerAndStartTimeIsAfterOrderByStartTimeDesc(
                        bookerFromDb, nowDateTime, pageable);
                break;
            }
            case WAITING: {
                result = bookingRepositoryJpa.findAllByBookerAndBookingStatusEqualsOrderByStartTimeDesc(
                        bookerFromDb, BookingStatus.WAITING, pageable);
                break;
            }
            case REJECTED: {
                result = bookingRepositoryJpa.findAllByBookerAndBookingStatusEqualsOrderByStartTimeDesc(
                        bookerFromDb, BookingStatus.REJECTED, pageable);
                break;
            }
            case UNKNOWN: {
                throw new UnsupportedOperationException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
        return result.stream()
                .map(BookingMapper::toBookingForResponse).collect(Collectors.toList());
    }

    @Override
    public List<BookingForResponse> getByOwnerId(Long userId, String state, Integer from, Integer size) {
        final LocalDateTime nowDateTime = LocalDateTime.now();
        BookingState bookingState;
        if (from < 0) {
            throw new BookingValidateException("Отрицательный параметр пагинации from = '" + from + "'.");
        }
        if (size < 1) {
            throw new BookingValidateException("Не верный параметр пагинации size = '" + size + "'.");
        }
        if (state.isBlank()) {
            bookingState = BookingState.ALL;
        } else {
            try {
                bookingState = BookingState.valueOf(state);
            } catch (IllegalArgumentException ex) {
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("startTime"));

        User bookerFromDb = userRepositoryJpa.findById(userId).orElseThrow(() -> new ObjectNotFoundException("При " +
                "получении списка бронирований не найден хозяин с ID = " + userId + " в БД."));
        List<Booking> result = new ArrayList<>();
        switch (bookingState) {
            case ALL: {
                result = bookingRepositoryJpa.findAllByItem_OwnerOrderByStartTimeDesc(bookerFromDb, pageable);
                break;
            }
            case CURRENT: {
                result = bookingRepositoryJpa.findAllBookingsItemByForOwnerWithStartAndEndTime(bookerFromDb, nowDateTime, nowDateTime, pageable);
                break;
            }
            case PAST: {
                result = bookingRepositoryJpa.findAllByItem_OwnerAndEndTimeIsBeforeOrderByStartTimeDesc(bookerFromDb, nowDateTime, pageable);
                break;
            }
            case FUTURE: {
                result = bookingRepositoryJpa.findAllByItem_OwnerAndStartTimeIsAfterOrderByStartTimeDesc(bookerFromDb, nowDateTime, pageable);
                break;
            }
            case WAITING: {
                result = bookingRepositoryJpa.findAllByItem_OwnerAndBookingStatusEqualsOrderByStartTimeDesc(
                        bookerFromDb, BookingStatus.WAITING, pageable);
                break;
            }
            case REJECTED: {
                result = bookingRepositoryJpa.findAllByItem_OwnerAndBookingStatusEqualsOrderByStartTimeDesc(
                        bookerFromDb, BookingStatus.REJECTED, pageable);
                break;
            }
            case UNKNOWN: {
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
        return result.stream()
                .map(BookingMapper::toBookingForResponse).collect(Collectors.toList());
    }

    private void validateBooking(BookingDto bookingDto, Item item, User booker) {
        if (item.getOwner().equals(booker)) {
            String message = "Создать бронь на свою вещь нельзя.";
            log.info(message);
            throw new ObjectNotFoundException(message);
        }
        if (bookingDto.getStartTime() == null || bookingDto.getEndTime() == null) {
            String message = "Начало или конец бронирования не указаны";
            log.info(message);
            throw new BookingValidateException(message);
        }
        if (bookingDto.getStartTime().equals(bookingDto.getEndTime())) {
            String message = "Начало и конец бронирования не могут быть одновременно";
            log.info(message);
            throw new BookingValidateException(message);
        }
        if (bookingDto.getStartTime().isBefore(LocalDateTime.now())) {
            String message = "Начало бронирования не может быть в прошлом" + bookingDto.getStartTime() + ".";
            log.info(message);
            throw new BookingValidateException(message);
        }
        if (bookingDto.getEndTime().isBefore(LocalDateTime.now())) {
            String message = "Окончание бронирования не может быть в прошлом.";
            log.info(message);
            throw new BookingValidateException(message);
        }
        if (bookingDto.getEndTime().isBefore(bookingDto.getStartTime())) {
            String message = "Окончание бронирования не может быть раньше его начала.";
            log.info(message);
            throw new BookingValidateException(message);
        }
        List<Booking> bookings = item.getBookings();
        if (bookings != null && !bookings.isEmpty()) {
            for (Booking b : bookings) {
                if (!(b.getEndTime().isBefore(bookingDto.getStartTime()) ||
                        b.getStartTime().isAfter(bookingDto.getStartTime()))) {
                    String message = "Найдено пересечение броней на эту вещь с name = " + item.getName() + ".";
                    log.info(message);
                    throw new BookingValidateException(message);
                }
            }
        }
    }
}
