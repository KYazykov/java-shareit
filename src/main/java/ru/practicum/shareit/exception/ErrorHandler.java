package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final ItemNotFoundException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка с предметом", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final ValidateException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка с валидацией", e.getMessage());
    }

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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemAvailableNotValidException(final ItemAvailableException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse("Предмет недоступен для аренды", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingNotValidException(final BookingValidateException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse("Введены неправильные данные для аренды вещи", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleForUnsupportedStatus(final UnsupportedStatusException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCommentNotValidException(final CommentValidateException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse("Введены неправильные данные для оставления комментария", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerException(final Exception e) {
        log.debug("Получен статус 500 INTERNAL_SERVER_ERROR {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка внутреннего сервера", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка валидации данных", e.getMessage());
    }
}
