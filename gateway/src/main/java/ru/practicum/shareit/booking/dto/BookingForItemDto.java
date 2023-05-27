package src.main.java.ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;
import src.main.java.ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
public class BookingForItemDto {

    private Long id;

    @JsonAlias({"start"})
    private LocalDateTime startTime;

    @JsonAlias({"end"})
    private LocalDateTime endTime;

    private Long bookerId;

    private BookingStatus status;

}
