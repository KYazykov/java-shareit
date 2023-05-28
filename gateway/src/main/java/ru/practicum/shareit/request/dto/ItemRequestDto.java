package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.user.UserForResponseDto;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Valid
public class ItemRequestDto {
    private Long id;
    @NotNull(groups = {CreateObject.class}, message = "Описание запроса вещи не может быть null.")
    @NotBlank(groups = {CreateObject.class}, message = "Описание запроса вещи не может быть пустым.")
    private String description;
    private UserForResponseDto requester;
    private LocalDateTime created;
}
