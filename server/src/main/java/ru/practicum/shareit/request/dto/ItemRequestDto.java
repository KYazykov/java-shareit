package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.user.UserForResponseDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private UserForResponseDto requester;
    private LocalDateTime created;
}
