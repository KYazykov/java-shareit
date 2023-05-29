package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
@Getter
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleForBadRequest(final ValidateException ex) {
        String error = "Error message";
        String message = ex.getMessage();
        log.error(error + " — " + message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error message" + ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleForUnsupportedStatus(final UnsupportedStatusException ex) {
        String error = "{\n\"error\":\"Unknown state: UNSUPPORTED_STATUS\",\n\"message\":\"UNSUPPORTED_STATUS\"\n}";
        String message = ex.getMessage();
        log.error(error + " — " + message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\n\"error\":\"Unknown state: " +
                "UNSUPPORTED_STATUS\",\n\"message\":\"UNSUPPORTED_STATUS\"\n}");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleForMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        String error = "Error 400. Не правильное значение аргумента.\t" + ex.getMessage();
        String message = ex.getMessage();
        log.error(error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error 400. Не правильное значение аргумента.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerException(final Exception e) {
        log.debug("Получен статус 500 INTERNAL_SERVER_ERROR {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка внутреннего сервера", e.getMessage());
    }
}