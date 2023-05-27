package src.main.java.ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.UserForResponseDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDto {

    private Long id;

    private Long itemId;

    private UserForResponseDto booker;
    @JsonAlias({"start"})
    private LocalDateTime startTime;
    @JsonAlias({"end"})
    private LocalDateTime endTime;
    @JsonProperty("status")
    private BookingStatus bookingStatus;
}
