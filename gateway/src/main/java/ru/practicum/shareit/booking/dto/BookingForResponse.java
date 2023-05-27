package src.main.java.ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import src.main.java.ru.practicum.shareit.booking.BookingStatus;
import src.main.java.ru.practicum.shareit.user.UserOnlyWithIdDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class BookingForResponse {


    private Long id;

    @JsonProperty("start")
    private LocalDateTime startTime;

    @JsonProperty("end")
    private LocalDateTime endTime;

    private ItemForResponseDto item;

    private UserOnlyWithIdDto booker;

    private BookingStatus status;
}
