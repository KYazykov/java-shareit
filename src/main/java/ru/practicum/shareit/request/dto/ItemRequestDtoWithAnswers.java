package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.user.UserForResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
public class ItemRequestDtoWithAnswers {
    Long id;
    String description;
    UserForResponseDto requester;
    LocalDateTime created;
    List<ItemForResponseDto> items;
}