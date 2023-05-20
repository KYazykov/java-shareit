package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImp implements ItemRequestService {
    private final UserRepositoryJpa userRepositoryJpa;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto addItemRequest(Long requesterId, ItemRequestDto itemRequestDto) {
        if (requesterId == null) {
            throw new ValidateException("Передан неверный параметр пользователя (ID = " + null + ").");
        }
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ItemNotFoundException("Описание не может быть пустым");
        }

        User userFromDb = userRepositoryJpa.findById(requesterId).orElseThrow(
                () -> new UserNotFoundException("Не найден пользователь (ID = '" + requesterId +
                        "') в БД при создании заявки на вещь."));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, userFromDb);
        itemRequest.setRequester(userFromDb);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestDtoWithAnswers> getItemRequestsByUserId(Long requesterId) {
        User requester = userRepositoryJpa.findById(requesterId).orElseThrow(
                () -> new UserNotFoundException("При выдаче списка запросов пользователя (ID = '"
                        + requesterId + "') этот пользователь не найден в БД."));
        List<ItemRequest> itemRequests = itemRequestRepository.getAllByRequester_IdOrderByCreatedDesc(requesterId);
        List<ItemRequestDtoWithAnswers> result = itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDtoWithAnswers).collect(Collectors.toList());
        log.info("Выдан ответ из репозитория о запросах пользователя с ID = '{}' ответах.", requesterId);
        return result;
    }

    @Override
    public List<ItemRequestDtoWithAnswers> getAllRequestForSee(Long userId, Integer from, Integer size) {
        if (from < 0) {
            throw new ValidateException("Отрицательный параметр пагинации from = '" + from + "'.");
        }
        if (size < 1) {
            throw new ValidateException("Не верный параметр пагинации size = '" + size + "'.");
        }
        User requester = userRepositoryJpa.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Произошла ошибка при выдаче списка всех запросов кроме " +
                        "запросов пользователя (ID = '" + userId + "'). Этот пользователь не найден в БД."));
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> itemRequests =
                itemRequestRepository.getItemRequestByRequesterIdIsNotOrderByCreated(userId, pageable);

        List<ItemRequestDtoWithAnswers> result = itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDtoWithAnswers).collect(Collectors.toList());
        log.info("Выдан ответ из репозитория о всех запросах для пользователя с ID = '{}'.", userId);
        return result;
    }

    @Override
    public ItemRequestDtoWithAnswers getItemRequestById(Long userId, Long requestId) {
        User user = userRepositoryJpa.findById(userId).orElseThrow(
                () -> new UserNotFoundException("При попытке выдачи запроса по ID в БД не найден " +
                        "пользователь, сделавший запрос."));
        if (requestId == null) {
            String message = "При попытке выдачи запроса по ID передан не правильный ID, равный null.";
            log.info(message);
            throw new ValidateException(message);
        }
        System.out.println(userId);
        System.out.println(requestId);
        ItemRequest result = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException("При попытке выдачи запроса по ID этот запрос не найден ."));
        log.info("Выдан запрос по его ID = '" + requestId + "'.");
        return ItemRequestMapper.toItemRequestDtoWithAnswers(result);
    }
}
