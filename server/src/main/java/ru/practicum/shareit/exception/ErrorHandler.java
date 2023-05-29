package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.UserController;

@Slf4j
@RestControllerAdvice(assignableTypes = {ItemController.class, UserController.class, BookingController.class,
        ItemRequestController.class})
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMethodArgumentNotValidException(final ObjectNotFoundException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        return new ErrorResponse("Объект не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMethodArgumentNotValidException(final UserNotFoundException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        return new ErrorResponse("Пользователь не найден", e.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerException(final Exception e) {
        log.debug("Получен статус 500 INTERNAL_SERVER_ERROR {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка внутреннего сервера", e.getMessage());
    }

}
