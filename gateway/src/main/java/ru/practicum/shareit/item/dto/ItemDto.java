package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Valid
public class ItemDto {
    private Long id;
    @NotBlank(groups = {CreateObject.class}, message =
            "При создании новой записи о вещи необходимо передать её название.")
    private String name;
    @NotBlank(groups = {CreateObject.class}, message =
            "При создании новой записи о вещи необходимо передать её описание.")
    private String description;
    @NotNull(groups = {CreateObject.class}, message =
            "При создании новой записи о вещи необходимо указать её статус бронирования.")
    private Boolean available;

    private Long requestId;


}

