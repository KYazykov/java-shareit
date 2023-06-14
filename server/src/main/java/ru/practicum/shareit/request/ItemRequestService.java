package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(Long requesterId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoWithAnswers> getItemRequestsByUserId(Long requesterId);

    List<ItemRequestDtoWithAnswers> getAllRequestForSee(Long requesterId, Integer from, Integer size);

    ItemRequestDtoWithAnswers getItemRequestById(Long userId, Long requestId);
}
