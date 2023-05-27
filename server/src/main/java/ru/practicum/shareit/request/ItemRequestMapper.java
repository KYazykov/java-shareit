package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.Collections;
import java.util.stream.Collectors;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toUserForResponse(itemRequest.getRequester()),
                itemRequest.getCreated());
    }

    public static ItemRequestDtoWithAnswers toItemRequestDtoWithAnswers(ItemRequest itemRequest) {
        return new ItemRequestDtoWithAnswers(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toUserForResponse(itemRequest.getRequester()),
                itemRequest.getCreated(),
                itemRequest.getItems().stream().map(o ->
                        new ItemForResponseDto(o.getId(), o.getName(), o.getDescription(), o.getAvailable(),
                                o.getRequestId())).collect(Collectors.toList()));
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User userFromDb) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                userFromDb,
                itemRequestDto.getCreated(),
                Collections.emptyList());
    }
}
