package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepositoryJpa;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.CommentValidateException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepositoryJpa;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryJpa itemRepositoryJpa;
    private final UserRepositoryJpa userRepository;
    private final CommentRepositoryJpa commentRepositoryJpa;
    private final BookingRepositoryJpa bookingRepositoryJpa;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;


    @Override
    public ItemDto updateItem(Long itemId, Long userId, Item item) {
        Item itemFromDB = itemRepositoryJpa.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Ошибка при обновлении вещи с ID = " + itemId
                        + " пользователя с ID = " + userId + " в БД. В БД отсутствует запись о вещи."));
        User ownerFromDB = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Ошибка при обновлении вещи с ID = " + itemId
                        + " пользователя с ID = " + userId + " в БД. В БД отсутствует запись о пользователе."));
        Long ownerIdFromDb = itemFromDB.getOwner().getId();
        if (ownerIdFromDb.equals(ownerFromDB.getId())) {
            if (item.getName() != null) {
                itemFromDB.setName(item.getName());
            }
            if (item.getDescription() != null) {
                itemFromDB.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                itemFromDB.setAvailable(item.getAvailable());
            }
            if (item.getRequestId() != null) {
                itemFromDB.setRequestId(item.getRequestId());
            }
        } else {
            String message = String.format("Error 404. 2. Обновление вещи невозможно, поскольку вещь с ID  = %d " +
                    "принадлежит пользователю с ID = %d.", itemId, ownerFromDB.getId());
            log.info(message);
            throw new ItemNotFoundException(message);
        }
        return ItemMapper.toItemDto(itemRepositoryJpa.save(itemFromDB));
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        if ((itemDto.getName() == null || itemDto.getName().isBlank())
                || itemDto.getAvailable() == null
                || (itemDto.getDescription() == null || itemDto.getDescription().isBlank())) {
            throw new ItemNotFoundException("Отстуствует имя, описание, или возможность для аренды вещи");
        }
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("В БД отсутствует запись о пользователе с ID = '" + userId +
                        "' при добавлении вещи в репозиторий"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepositoryJpa.save(item));
    }

    @Override
    public List<ItemDtoWithBookingAndComments> getItems(Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Ошибка при получении списка вещей пользователя с ID = " + userId
                        + "в БД. В БД отсутствует запись о пользователе."));
        List<Item> resultItems = itemRepositoryJpa.findAllByOwnerOrderById(owner);
        List<ItemDtoWithBookingAndComments> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Item i : resultItems) {
            ItemDtoWithBookingAndComments itemWithBAndC = ItemMapper.toItemDtoWithBookingAndComments(i);
            List<Booking> bookings = i.getBookings();
            if (!(bookings.isEmpty())) {
                Booking lastBooking = findLastBookingByDate(bookings, now);
                Booking nextBooking = findNextBookingByDate(bookings, now);
                if (lastBooking != null && lastBooking.getBookingStatus().equals(BookingStatus.APPROVED)) {
                    itemWithBAndC.setLastBooking(bookingMapper.toBookingForItemDto(lastBooking));
                }
                if (nextBooking != null && (nextBooking.getBookingStatus().equals(BookingStatus.APPROVED) ||
                        nextBooking.getBookingStatus().equals(BookingStatus.WAITING))) {
                    itemWithBAndC.setNextBooking(bookingMapper.toBookingForItemDto(nextBooking));
                }
            }
            if (itemWithBAndC.getComments() != null && !itemWithBAndC.getComments().isEmpty()) {
                List<Comment> comments = commentRepositoryJpa.findAllByItemOrderById(i);
                List<CommentDto> commentDtos = comments.stream()
                        .map(CommentMapper::toCommentDto).collect(Collectors.toList());
                itemWithBAndC.setComments(commentDtos);
            } else {
                itemWithBAndC.setComments(new ArrayList<>());
            }
            result.add(itemWithBAndC);
        }
        return result;
    }

    @Override
    public ItemForResponseDto getItem(Long itemId) {
        return ItemMapper.toItemForResponseDto(itemRepositoryJpa.findById(itemId)
                .orElseThrow(()
                        -> new ObjectNotFoundException("Error 404. Запись о вещи с Id = " + itemId + " не найдена в БД.")));
    }


    @Override
    public void removeItem(Long itemId) {
        itemRepositoryJpa.deleteById(itemId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> selectedItems = new ArrayList<>();
        if (text.isBlank()) {
            return List.of();
        }
        for (Item item : itemRepositoryJpa.findAll()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()) {
                selectedItems.add(ItemMapper.toItemDto(item));
            }
        }
        return selectedItems;
    }

    @Override
    public ItemDtoWithBookingAndComments getItemWithBookingAndComment(Long itemId, Long ownerId) {
        Item itemFromBd = itemRepositoryJpa.findById(itemId)
                .orElseThrow(() -> new UserNotFoundException("Ошибка при получении списка вещей пользователя с ID = "
                        + ownerId + "в БД. В БД отсутствует запись о пользователе."));
        List<Booking> allBookings = bookingRepositoryJpa.findAllByItemOrderByStartTimeDesc(itemFromBd);
        Booking lastBooking = null;
        Booking nextBooking = null;
        LocalDateTime now = LocalDateTime.now();

        ItemDtoWithBookingAndComments itemWithBAndCDto = ItemMapper.toItemDtoWithBookingAndComments(itemFromBd);
        Long ownerIdForItemFromBd = itemFromBd.getOwner().getId();
        if (ownerIdForItemFromBd.equals(ownerId) && allBookings != null && !(allBookings.isEmpty())) {
            nextBooking = findNextBookingByDate(allBookings, now);
            lastBooking = findLastBookingByDate(allBookings, now);
            if (nextBooking != null && (nextBooking.getBookingStatus().equals(BookingStatus.APPROVED) ||
                    nextBooking.getBookingStatus().equals(BookingStatus.WAITING))) {
                itemWithBAndCDto.setNextBooking(bookingMapper.toBookingForItemDto(nextBooking));
            }
            if (lastBooking != null && lastBooking.getBookingStatus().equals(BookingStatus.APPROVED)) {
                itemWithBAndCDto.setLastBooking(bookingMapper.toBookingForItemDto(lastBooking));
            }
        }
        List<CommentDto> commentDtoForResponse = new ArrayList<>();
        List<Comment> commentsFromDb = commentRepositoryJpa.findAllByItemOrderById(itemFromBd);

        if (commentsFromDb != null && !(commentsFromDb.isEmpty())) {
            commentDtoForResponse = commentsFromDb.stream()
                    .map(CommentMapper::toCommentDto).collect(Collectors.toList());
        } else {
            itemWithBAndCDto.setComments(new ArrayList<>());
        }
        itemWithBAndCDto.setComments(commentDtoForResponse);
        return itemWithBAndCDto;
    }

    @Override
    public CommentDto saveComment(Long bookerId, Long itemId, CommentDto commentDto) {
        User userFromBd = userRepository.findById(bookerId).orElseThrow(() ->
                new UserNotFoundException("Ошибка при сохранении комментария к вещи с ID = " + itemId
                        + " пользователем с ID = " + bookerId + " в БД. В БД отсутствует запись о пользователе."));
        Item itemFromBd = itemRepositoryJpa.findById(
                itemId).orElseThrow(() ->
                new ItemNotFoundException("Ошибка при сохранении комментария к вещи с ID = " + itemId
                        + " пользователем с ID = " + bookerId + " в БД. В БД отсутствует запись о вещи."));
        List<Booking> bookings = itemFromBd.getBookings();

        boolean isBooker = false;

        if (bookings != null) {
            for (Booking b : bookings) {
                Long bookerIdFromBooking = b.getBooker().getId();
                if (bookerIdFromBooking.equals(bookerId) && b.getEndTime().isBefore(LocalDateTime.now())) {
                    isBooker = true;
                    break;
                }
            }
        }
        if (!isBooker) {
            throw new CommentValidateException("Ошибка при сохранении комментария к вещи с ID = " + itemId
                    + " пользователем с ID = " + bookerId + " в БД. Пользователь не арендовал эту вещь.");
        }
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new CommentValidateException("Ошибка при сохранении комментария к вещи, комментарий пустой");
        }
        commentDto.setItemId(itemId);
        commentDto.setAuthorName(userFromBd.getName());
        Comment commentForSave = commentMapper.toComment(commentDto);
        commentForSave.setItem(itemFromBd);
        commentForSave.setAuthor(userFromBd);
        commentForSave.setCreated(LocalDateTime.now());
        Comment resComment = commentRepositoryJpa.save(commentForSave);
        return CommentMapper.toCommentDto(resComment);
    }

    private Booking findNextBookingByDate(List<Booking> bookings, LocalDateTime now) {
        Booking first = null;
        if (bookings != null && !bookings.isEmpty()) {
            for (Booking b : bookings)
                if (b.getStartTime().isAfter(now)) {
                    if (first == null && (b.getBookingStatus().equals(BookingStatus.APPROVED)
                            || b.getBookingStatus().equals(BookingStatus.WAITING)))
                        first = b;
                    else if (first == null) first = b;
                    else if (b.getStartTime().isBefore(first.getStartTime())) first = b;
                }
        }
        return first;
    }

    private Booking findLastBookingByDate(List<Booking> bookings, LocalDateTime now) {
        Booking last = null;

        if (bookings != null && !bookings.isEmpty()) for (Booking b : bookings)
            if (b.getEndTime().isBefore(now) || (b.getStartTime().isBefore(now) && b.getEndTime().isAfter(now))) {
                if (last == null && (b.getBookingStatus().equals(BookingStatus.APPROVED))) last = b;
                else if (last == null) last = b;
                else if (b.getEndTime().isAfter(last.getEndTime())) last = b;
            }
        return last;
    }
}
