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
    private Long id;
    private String description;
    private UserForResponseDto requester;
    private LocalDateTime created;
    private List<ItemForResponseDto> items;
}