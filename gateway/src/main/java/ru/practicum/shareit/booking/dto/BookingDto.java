package src.main.java.ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import src.main.java.ru.practicum.shareit.booking.BookingStatus;
import src.main.java.ru.practicum.shareit.user.UserForResponseDto;
import src.main.java.ru.practicum.shareit.validation.CreateObject;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@Valid
public class BookingDto {

    private Long id;
    @NotNull(groups = {CreateObject.class}, message = "При создании брони должна быть информация о вещи.")
    private Long itemId;

    private UserForResponseDto booker;
    @JsonAlias({"start"})
    @FutureOrPresent(groups = {CreateObject.class}, message = "Окончание бронирования должно быть в будущем.")
    private LocalDateTime startTime;
    @JsonAlias({"end"})
    @Future(groups = {CreateObject.class}, message = "Окончание бронирования должно быть в будущем.")
    private LocalDateTime endTime;
    @JsonProperty("status")
    private BookingStatus bookingStatus;
}
