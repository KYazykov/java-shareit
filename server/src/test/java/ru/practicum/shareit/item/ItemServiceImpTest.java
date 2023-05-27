package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepositoryJpa;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepositoryJpa;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImpTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private final EntityManager em;
    @Autowired
    private final CommentRepositoryJpa commentRepositoryJpa;
    ItemRequest itemRequest1;
    UserDto ownerDto1;
    User owner1;
    UserDto requesterDto101;
    User requester101;
    UserDto bookerDto;
    User booker;
    UserDto userDtoForTest;
    User userForTest;
    LocalDateTime now;
    LocalDateTime nowPlus10min;
    LocalDateTime nowPlus10hours;
    Item item1;
    ItemDto itemDto1;
    ItemRequestDto itemRequestDto1;
    Booking booking1;
    BookingDto bookingDto1;
    CommentDto commentDto;
    /**
     * Запрос всех вещей из БД.
     */
    TypedQuery<Item> query;
    @Autowired
    private ItemRepositoryJpa itemRepositoryJpa;
    @Autowired
    private UserRepositoryJpa userRepositoryJpa;
    @Autowired
    private BookingRepositoryJpa bookingRepositoryJpa;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        nowPlus10min = now.plusMinutes(10);
        nowPlus10hours = now.plusHours(10);

        ownerDto1 = UserDto.builder()
                .name("name ownerDto1")
                .email("ownerDto1@mans.gf")
                .build();

        owner1 = User.builder()
                .id(ownerDto1.getId())
                .name(ownerDto1.getName())
                .email(ownerDto1.getEmail())
                .comments(List.of())
                .bookings(List.of())
                .userItems(List.of())
                .build();

        requesterDto101 = UserDto.builder()
                .name("name requesterDto101")
                .email("requesterDto101@mans.gf")
                .build();

        requester101 = User.builder()
                .id(requesterDto101.getId())
                .name(requesterDto101.getName())
                .email(requesterDto101.getEmail())
                .comments(List.of())
                .bookings(List.of())
                .userItems(List.of())
                .build();

        userDtoForTest = UserDto.builder()
                .name("name userDtoForTest")
                .email("userDtoForTest@userDtoForTest.zx")
                .build();

        userForTest = User.builder()
                .name(userDtoForTest.getName())
                .email(userDtoForTest.getEmail())
                .comments(List.of())
                .bookings(List.of())
                .userItems(List.of())
                .build();

        bookerDto = UserDto.builder()
                .name("booker")
                .email("booker@wa.dzd")
                .build();

        booker = User.builder()
                .name(bookerDto.getName())
                .email(bookerDto.getEmail())
                .comments(new ArrayList<>())
                .bookings(new ArrayList<>())
                .userItems(new ArrayList<>())
                .build();

        itemRequest1 = ItemRequest.builder()
                .description("description for request 1")
                .requester(requester101)
                .created(now)
                .build();

        item1 = Item.builder()
                .name("name for item 1")
                .description("description for item 1")
                .owner(owner1)
                .available(true)
                .comments(new ArrayList<>())
                .bookings(new ArrayList<>())
                .build();

        itemDto1 = ItemDto.builder()
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .build();

        itemRequestDto1 = ItemRequestDto.builder()
                .description(item1.getDescription())
                .requester(UserMapper.toUserForResponse(requester101))
                .created(now)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .created(now)
                .text("comment 1")
                .authorName(userForTest.getName())
                .build();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addItem_whenAllAreOk_returnSavedItemDto() {
        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);

        query =
                em.createQuery("Select i from Item i", Item.class);
        List<Item> beforeSave = query.getResultList();

        assertEquals(0, beforeSave.size());

        ItemDto savedItemDto = itemService.addItem(savedOwnerDto1.getId(), itemDto1);
        List<Item> afterSave = query.getResultList();

        assertEquals(1, afterSave.size());
        assertEquals(savedItemDto.getId(), afterSave.get(0).getId());
        assertEquals(savedItemDto.getRequestId(), afterSave.get(0).getRequestId());
        assertEquals(savedItemDto.getDescription(), afterSave.get(0).getDescription());
        assertEquals(savedItemDto.getName(), afterSave.get(0).getName());
    }

    @Test
    void addItem_whenUserNotFound_returnNotFoundRecordInDb() {
        assertThrows(UserNotFoundException.class, () -> itemService.addItem(10000L, itemDto1));
    }

    @Test
    void getItemsByUserId_whenOk_returnItemDtoList() {
        itemDto1.setBookings(Collections.emptyList());
        itemDto1.setComments(Collections.emptyList());
        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);
        ItemDto savedItemDto = itemService.addItem(savedOwnerDto1.getId(), itemDto1);
        List<ItemDtoWithBookingAndComments> itemDtos = itemService.getItems(savedOwnerDto1.getId());

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDto.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDto.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDto.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDto.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDto.getAvailable(), itemDtos.get(0).getAvailable());
    }

    @Test
    void getItemsByUserId_whenUserNotFoundInBD_returnException() {
        assertThrows(UserNotFoundException.class, () -> itemService.getItems(1000L));
    }

    @Test
    void updateInStorage_whenAllIsOk_returnItemFromDb() {
        itemDto1.setBookings(Collections.emptyList());
        itemDto1.setComments(Collections.emptyList());
        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);
        ItemDto savedItemDtoBeforeUpd = itemService.addItem(savedOwnerDto1.getId(), itemDto1);
        List<ItemDtoWithBookingAndComments> itemDtos = itemService.getItems(savedOwnerDto1.getId());

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDtoBeforeUpd.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDtoBeforeUpd.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), itemDtos.get(0).getAvailable());

        ItemDto updatedItem = savedItemDtoBeforeUpd.toBuilder()
                .name("new name")
                .description("new description")
                .requestId(55L).build();
        ItemDto savedUpdItem =
                itemService.updateItem(savedItemDtoBeforeUpd.getId(), savedOwnerDto1.getId(), ItemMapper.toItem(updatedItem));

        assertNotEquals(savedItemDtoBeforeUpd.getName(), savedUpdItem.getName());
        assertNotEquals(savedItemDtoBeforeUpd.getDescription(), savedUpdItem.getDescription());
        assertEquals(savedItemDtoBeforeUpd.getId(), savedUpdItem.getId());
        assertEquals(55L, savedUpdItem.getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), savedUpdItem.getAvailable());
    }

    @Test
    void updateInStorage_whenAllFieldsItemIsNull_returnItemFromDb() {
        itemDto1.setBookings(Collections.emptyList());
        itemDto1.setComments(Collections.emptyList());
        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);
        ItemDto savedItemDtoBeforeUpd = itemService.addItem(savedOwnerDto1.getId(), itemDto1);
        List<ItemDtoWithBookingAndComments> itemDtos = itemService.getItems(savedOwnerDto1.getId());

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDtoBeforeUpd.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDtoBeforeUpd.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), itemDtos.get(0).getAvailable());

        ItemDto updatedItem = savedItemDtoBeforeUpd.toBuilder()
                .name(null)
                .description(null)
                .requestId(null)
                .available(null).build();

        ItemDto savedUpdItem =
                itemService.updateItem(savedItemDtoBeforeUpd.getId(), savedOwnerDto1.getId(), ItemMapper.toItem(updatedItem));

        assertEquals(savedItemDtoBeforeUpd.getName(), savedUpdItem.getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), savedUpdItem.getDescription());
        assertEquals(savedItemDtoBeforeUpd.getId(), savedUpdItem.getId());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), savedUpdItem.getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), savedUpdItem.getAvailable());
    }

    @Test
    void updateInStorage_whenUpdatedItemHasOtherUser_returnNotFoundRecordInBD() {
        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);
        UserDto savedOwnerDto2 = userService.addUser(userDtoForTest);
        ItemDto savedItemDtoBeforeUpd = itemService.addItem(savedOwnerDto2.getId(), itemDto1);
        List<ItemDtoWithBookingAndComments> itemDtos = itemService.getItems(savedOwnerDto1.getId());

        assertEquals(0, itemDtos.size());

        ItemDto updatedItem = savedItemDtoBeforeUpd.toBuilder().name("new name")
                .description("new description").requestId(55L).build();
        assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(savedItemDtoBeforeUpd.getId(),
                        savedOwnerDto1.getId(), ItemMapper.toItem(updatedItem)));
    }

    @Test
    void updateInStorage_whenItemNotFoundInDb_returnNotFoundRecordInBD() {
        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);
        Long itemId = 1001L;
        ItemNotFoundException ex = assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(itemId, savedOwnerDto1.getId(), ItemMapper.toItem(itemDto1)));
        assertEquals(String.format("Ошибка при обновлении вещи с ID = %d пользователя с ID = %d " +
                "в БД. В БД отсутствует запись о вещи.", itemId, savedOwnerDto1.getId()), ex.getMessage());
    }

    @Test
    void getItemById_whenOk_returnItemFromDb() {
        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);
        ItemDto savedItemDtoBeforeUpd = itemService.addItem(savedOwnerDto1.getId(), itemDto1);
        ItemForResponseDto itemDtoFromBd = itemService.getItem(savedItemDtoBeforeUpd.getId());

        assertEquals(savedItemDtoBeforeUpd.getId(), itemDtoFromBd.getId());
        assertEquals(savedItemDtoBeforeUpd.getName(), itemDtoFromBd.getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), itemDtoFromBd.getDescription());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), itemDtoFromBd.getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), itemDtoFromBd.getAvailable());
    }

    @Test
    void getItemById_whenWrongUser_returnItemFromDb() {
        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);
        ItemDto savedItemDto = itemService.addItem(savedOwnerDto1.getId(), itemDto1);
        assertThrows(ObjectNotFoundException.class, () -> itemService.getItem(savedItemDto.getId() + 1));
    }

    @Test
    void removeItemById() {
        itemDto1.setBookings(Collections.emptyList());
        itemDto1.setComments(Collections.emptyList());
        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);
        ItemDto savedItemDtoBeforeDel = itemService.addItem(savedOwnerDto1.getId(), itemDto1);
        List<ItemDtoWithBookingAndComments> itemDtos = itemService.getItems(savedOwnerDto1.getId());

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDtoBeforeDel.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDtoBeforeDel.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDtoBeforeDel.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDtoBeforeDel.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDtoBeforeDel.getAvailable(), itemDtos.get(0).getAvailable());

        itemService.removeItem(savedItemDtoBeforeDel.getId());

        List<ItemDtoWithBookingAndComments> itemDtosAfterDel =
                itemService.getItems(savedOwnerDto1.getId());

        assertEquals(0, itemDtosAfterDel.size());
    }

    @Test
    void testSearchItemsByText() {
        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);
        ItemDto savedItemDto01 = itemService.addItem(savedOwnerDto1.getId(), itemDto1);

        UserDto savedRequester = userService.addUser(requesterDto101);
        ItemDto itemDto02 = itemDto1.toBuilder().name("new item").description("new description").build();

        ItemDto savedItemDto02 = itemService.addItem(savedOwnerDto1.getId(), itemDto02);

        List<ItemDto> itemDtoList = itemService.searchItems("nEw", 0, 10);

        assertNotNull(itemDtoList);
        assertEquals(1, itemDtoList.size());
        assertEquals(itemDto02.getDescription(), itemDtoList.stream().findFirst().get().getDescription());
    }

    @Test
    void searchItemsByText_whenTextIsBlank() {
        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);
        ItemDto savedItemDto01 = itemService.addItem(savedOwnerDto1.getId(), itemDto1);

        UserDto savedRequester = userService.addUser(requesterDto101);
        ItemDto itemDto02 = itemDto1.toBuilder().name("new item").description("new description").build();

        ItemDto savedItemDto02 = itemService.addItem(savedOwnerDto1.getId(), itemDto02);

        List<ItemDto> itemDtoList = itemService.searchItems("", 0, 10);

        assertNotNull(itemDtoList);
        assertEquals(0, itemDtoList.size());
    }

    @Test
    void getItemWithBookingAndComment() {
        itemDto1.setBookings(Collections.emptyList());
        itemDto1.setComments(Collections.emptyList());
        UserDto savedBooker = userService.addUser(bookerDto);
        booker.setId(savedBooker.getId());
        bookerDto.setId(savedBooker.getId());
        UserForResponseDto bookerForResponse = UserMapper.toUserForResponse(booker);
        assertEquals(savedBooker.getId(), booker.getId());
        assertEquals(savedBooker.getName(), booker.getName());
        assertEquals(savedBooker.getEmail(), booker.getEmail());


        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);
        owner1.setId(savedOwnerDto1.getId());
        ownerDto1.setId(savedOwnerDto1.getId());
        assertEquals(savedOwnerDto1.getId(), owner1.getId());
        assertEquals(savedOwnerDto1.getName(), owner1.getName());
        assertEquals(savedOwnerDto1.getEmail(), owner1.getEmail());

        ItemDto savedItemDto01 = itemService.addItem(savedOwnerDto1.getId(), itemDto1);
        itemDto1.setId(savedItemDto01.getId());
        item1.setId(savedItemDto01.getId());
        assertEquals(savedItemDto01.getId(), item1.getId());
        assertEquals(savedItemDto01.getName(), item1.getName());
        assertEquals(savedItemDto01.getDescription(), item1.getDescription());

        bookingDto1 = BookingDto.builder()
                .itemId(item1.getId())
                .booker(bookerForResponse)
                .startTime(now.plusSeconds(1)).endTime(now.plusSeconds(2))
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        booking1 = Booking.builder()
                .item(item1)
                .booker(booker)
                .startTime(bookingDto1.getStartTime()).endTime(bookingDto1.getEndTime())
                .bookingStatus(bookingDto1.getBookingStatus())
                .build();


        BookingForResponse savedBookingForResponse = bookingService.createBooking(bookerDto.getId(), bookingDto1);
        booking1.setId(savedBookingForResponse.getId());
        bookingDto1.setId(savedBookingForResponse.getId());

        item1.setBookings(List.of(booking1));
        assertDoesNotThrow(() -> Thread.sleep(1500));

        Comment comment1 = Comment.builder().text("content commentary").item(item1).author(booker)
                .created(LocalDateTime.now()).build();
        Comment savedComment1 = commentRepositoryJpa.save(comment1);
        comment1.setId(savedComment1.getId());

        ItemDtoWithBookingAndComments result =
                itemService.getItemWithBookingAndComment(item1.getId(), owner1.getId());

        assertEquals(item1.getName(), result.getName());
        assertEquals(item1.getDescription(), result.getDescription());
        assertEquals(item1.getRequestId(), result.getRequestId());
        assertEquals(item1.getAvailable(), result.getAvailable());
        assertEquals(comment1.getText(), result.getComments().get(0).getText());
        assertEquals(comment1.getAuthor().getName(), result.getComments().get(0).getAuthorName());
    }

    @Test
    void getItemWithBookingAndComment_whenAllIsOk_returnItemDtoWithBookingAndComments() {
        itemDto1.setBookings(Collections.emptyList());
        itemDto1.setComments(Collections.emptyList());
        UserDto savedBooker = userService.addUser(bookerDto);
        booker.setId(savedBooker.getId());
        bookerDto.setId(savedBooker.getId());
        UserForResponseDto bookerForResponse = UserMapper.toUserForResponse(booker);


        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);
        owner1.setId(savedBooker.getId());
        ownerDto1.setId(savedBooker.getId());

        ItemDto savedItemDto01 = itemService.addItem(savedOwnerDto1.getId(), itemDto1);
        itemDto1.setId(savedItemDto01.getId());
        item1.setId(savedItemDto01.getId());

        bookingDto1 = BookingDto.builder()
                .itemId(item1.getId())
                .booker(bookerForResponse)
                .startTime(now.plusSeconds(1)).endTime(now.plusSeconds(2))
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        BookingForResponse savedBookingForResponse = bookingService.createBooking(bookerDto.getId(), bookingDto1);

        booking1 = Booking.builder().id(savedBookingForResponse.getId())
                .item(item1)
                .booker(booker)
                .startTime(bookingDto1.getStartTime())
                .endTime(bookingDto1.getEndTime())
                .bookingStatus(bookingDto1.getBookingStatus())
                .build();
        assertDoesNotThrow(() -> Thread.sleep(1500));

        Comment comment1 = Comment.builder().text("content commentary").item(item1).author(booker)
                .created(LocalDateTime.now()).build();
        Comment savedComment1 = commentRepositoryJpa.save(comment1);

        ItemDtoWithBookingAndComments result =
                itemService.getItemWithBookingAndComment(item1.getId(), owner1.getId());

        assertEquals(item1.getName(), result.getName());
        assertEquals(item1.getDescription(), result.getDescription());
        assertEquals(item1.getRequestId(), result.getRequestId());
        assertEquals(item1.getAvailable(), result.getAvailable());
    }

    @Test
    void getItemWithBookingAndComment_whenOwnerNotFound_returnNotFoundRecordInBD() {
        itemDto1.setBookings(Collections.emptyList());
        itemDto1.setComments(Collections.emptyList());
        UserDto savedOwnerDto1 = userService.addUser(ownerDto1);
        ItemDto savedItemDto01 = itemService.addItem(savedOwnerDto1.getId(), itemDto1);
        Long ownerId = 1001L;

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () ->
                itemService.getItemWithBookingAndComment(savedItemDto01.getId(), ownerId));
        assertEquals(String.format("Ошибка при обновлении вещи с ID = %d пользователя с ID = %d в БД. В БД " +
                "отсутствует запись о пользователе.", savedItemDto01.getId(), ownerId), ex.getMessage());
    }

    @Test
    void saveComment_thenReturnExceptions() {
        UserDto savedUser1 = userService.addUser(ownerDto1);
        UserDto savedUser2 = userService.addUser(userDtoForTest);
        ItemDto savedItem = itemService.addItem(savedUser1.getId(), itemDto1);
        CommentDto commentDto = CommentDto.builder()
                .authorName(savedUser2.getName())
                .text("comment from user 1")
                .created(now)
                .build();

        bookingDto1 = BookingDto.builder()
                .itemId(savedItem.getId())
                .startTime(now.minusDays(10))
                .booker(new UserForResponseDto(savedUser1.getId(), savedUser1.getName()))
                .bookingStatus(BookingStatus.WAITING)
                .endTime(now.minusDays(8))
                .build();

        BookingForResponse savedBookingDto;
        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.createBooking(savedUser1.getId(), bookingDto1);
        });
        assertThrows(BookingValidateException.class, () -> {
            bookingService.createBooking(savedUser2.getId(), bookingDto1);
        });

        bookingDto1.setStartTime(now.plusHours(1));
        bookingDto1.setEndTime(now.plusHours(111));
        bookingService.createBooking(savedUser2.getId(), bookingDto1);

        CommentValidateException ex = assertThrows(CommentValidateException.class,
                () -> itemService.saveComment(savedUser1.getId(), savedItem.getId(), commentDto));
        assertEquals(String.format("Ошибка при сохранении комментария к вещи с ID = %d пользователем с ID " +
                        "= %d в БД. Пользователь не арендовал эту вещь.", savedItem.getId(), savedUser1.getId()),
                ex.getMessage());
    }

    @Test
    void saveComment_whenItemNotFound_thenReturnNotFoundRecordInDb() {
        UserDto savedUser1 = userService.addUser(ownerDto1);
        UserDto savedUser2 = userService.addUser(userDtoForTest);
        CommentDto commentDto = CommentDto.builder()
                .authorName(savedUser2.getName())
                .text("comment from user 1")
                .created(now)
                .build();
        Long notFoundItemId = 1001L;
        ItemNotFoundException ex = assertThrows(ItemNotFoundException.class,
                () -> itemService.saveComment(savedUser1.getId(), notFoundItemId, commentDto));
        assertEquals(String.format("Ошибка при сохранении комментария к вещи с ID = %d пользователем с ID " +
                        "= %d в БД. В БД отсутствует запись о вещи.",
                notFoundItemId, savedUser1.getId()), ex.getMessage());
    }

    @Test
    void saveComment_whenAllAreOk_thenReturnComment() {
        CommentDto inputCommentDto = CommentDto.builder().id(1L).text("new comment for test").build();

        User owner2 = User.builder()
                .id(2L)
                .name("name for owner")
                .email("owner2@aadmf.wreew")
                .build();

        User userForTest2 = User.builder()
                .id(1L)
                .name("name user for test 2")
                .email("userForTest2@ahd.ew")
                .build();

        Item zaglushka = Item.builder().id(1L).name("zaglushka").description("desc item zaglushka")
                .owner(owner2).build();

        Booking bookingFromBd = Booking.builder()
                .id(1L)
                .item(zaglushka)
                .booker(userForTest2)
                .startTime(now.minusDays(10))
                .endTime(now.minusDays(5))
                .build();

        Item itemFromBd = Item.builder()
                .id(1L)
                .name("name for item")
                .description("desc for item")
                .owner(owner2)
                .available(true)
                .bookings(List.of(bookingFromBd))
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("comment 1")
                .authorName("name user for test 2")
                .created(now.minusDays(5))
                .build();

        Comment outputComment = Comment.builder()
                .id(1L)
                .author(userForTest2)
                .text("comment 1")
                .item(itemFromBd)
                .build();

        UserRepositoryJpa userRepositoryJpa2 = mock(UserRepositoryJpa.class);
        ItemRepositoryJpa itemRepositoryJpa2 = mock(ItemRepositoryJpa.class);
        CommentRepositoryJpa commentRepository2 = mock(CommentRepositoryJpa.class);

        ItemService itemService2 = new ItemServiceImpl(itemRepositoryJpa2,
                userRepositoryJpa2, commentRepository2, bookingRepositoryJpa);

        when(userRepositoryJpa2.findById(any()))
                .thenReturn(Optional.of(userForTest2));
        when(itemRepositoryJpa2.findById(any()))
                .thenReturn(Optional.of(itemFromBd));
        when(commentRepository2.save(any()))
                .thenReturn(outputComment);

        CommentDto outputCommentDto =
                itemService2.saveComment(userForTest2.getId(), itemFromBd.getId(), inputCommentDto);

        assertEquals(commentDto.getText(), outputCommentDto.getText());
        assertEquals(commentDto.getAuthorName(), outputCommentDto.getAuthorName());
        assertEquals(commentDto.getId(), outputCommentDto.getId());
        assertNotEquals(commentDto.getCreated(), outputCommentDto.getCreated());
    }

    @Test
    void saveComment_whenContentIsBlank_thenReturnValidateException() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("")
                .authorName("name user for test 2")
                .created(now.minusDays(5))
                .build();

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> itemService.saveComment(1L, 1L, commentDto));
        assertEquals("Ошибка при сохранении комментария к вещи с ID = 1 пользователем с ID = 1 в БД. " +
                "В БД отсутствует запись о пользователе.", ex.getMessage());
    }

    @Test
    void saveComment_whenUserNotFound_thenReturnNotFoundRecordInBD() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("comment 1")
                .authorName("name user for test 2")
                .created(now.minusDays(5))
                .build();

        assertThrows(UserNotFoundException.class, () -> itemService.saveComment(1000L, 1L, commentDto));
    }

    @Test
    void saveComment_when() {
        CommentDto inputCommentDto = CommentDto.builder().id(1L).text("new comment for test").build();

        User owner2 = User.builder()
                .id(2L)
                .name("name for owner")
                .email("owner2@aadmf.wreew")
                .build();

        User userForTest2 = User.builder()
                .id(1L)
                .name("name user for test 2")
                .email("userForTest2@ahd.ew")
                .build();

        Item zaglushka = Item.builder().id(1L).name("zaglushka").description("desc item zaglushka")
                .owner(owner2).build();

        Booking bookingFromBd = Booking.builder()
                .id(1L)
                .item(zaglushka)
                .booker(userForTest2)
                .startTime(now.minusDays(10))
                .endTime(now.minusDays(5))
                .build();

        Item itemFromBd = Item.builder()
                .id(1L)
                .name("name for item")
                .description("desc for item")
                .owner(owner2)
                .available(true)
                .bookings(List.of(bookingFromBd))
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("comment 1")
                .authorName("name user for test 2")
                .created(now.minusDays(5))
                .build();

        Comment outputComment = Comment.builder()
                .id(1L)
                .author(userForTest2)
                .text("comment 1")
                .item(itemFromBd)
                .build();

        UserRepositoryJpa userRepositoryJpa2 = mock(UserRepositoryJpa.class);
        ItemRepositoryJpa itemRepositoryJpa2 = mock(ItemRepositoryJpa.class);
        CommentRepositoryJpa commentRepository2 = mock(CommentRepositoryJpa.class);
        ItemService itemService2 = new ItemServiceImpl(itemRepositoryJpa2,
                userRepositoryJpa2, commentRepository2, bookingRepositoryJpa);

        when(userRepositoryJpa2.findById(any()))
                .thenReturn(Optional.of(userForTest2));
        when(itemRepositoryJpa2.findById(any()))
                .thenReturn(Optional.of(itemFromBd));
        when(commentRepository2.save(any()))
                .thenReturn(outputComment);

        CommentDto outputCommentDto =
                itemService2.saveComment(userForTest2.getId(), itemFromBd.getId(), inputCommentDto);

        assertEquals(commentDto.getText(), outputCommentDto.getText());
        assertEquals(commentDto.getAuthorName(), outputCommentDto.getAuthorName());
        assertEquals(commentDto.getId(), outputCommentDto.getId());
        assertNotEquals(commentDto.getCreated(), outputCommentDto.getCreated());

    }

    @Test
    void commentToDto_whenCommentIsOk_returnCommentDto() {
        Comment comment = Comment.builder()
                .id(0L)
                .author(booker)
                .created(now)
                .item(item1)
                .text("comment").build();
        CommentDto commentDto1 = CommentMapper.toCommentDto(comment);
        assertEquals(comment.getId(), commentDto1.getId());
        assertEquals(comment.getText(), commentDto1.getText());
        assertEquals(comment.getAuthor().getName(), commentDto1.getAuthorName());
        assertEquals(comment.getCreated(), commentDto1.getCreated());
    }

    @Test
    void commentToDto_whenCommentIsNull() {
        Comment comment = null;
        assertThrows(NullPointerException.class,
                () -> CommentMapper.toCommentDto(comment));
    }

    @Test
    void dtoToModel_whenAuthorIsNullAndCreatedDateIsNull() {
        commentDto.setAuthorName(null);
        Comment comment1 = CommentMapper.toComment(commentDto, item1);
        assertEquals(commentDto.getId(), comment1.getId());
        assertEquals(commentDto.getText(), comment1.getText());
        assertEquals(commentDto.getCreated(), comment1.getCreated());
        assertNotNull(comment1.getAuthor().getName());
    }

    @Test
    void itemWithBookingAndCommentsDto_whenItemIsNull_returnNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () ->
                ItemMapper.toItemDtoWithBookingAndComments(null));
    }

    @Test
    void itemWithBookingAndCommentsDto_mapToDto_whenAllIsOkWithComments_returnItemDtoWithBookingAndComments() {
        Comment comment1 = Comment.builder().id(1L).item(item1).created(now.minusDays(1))
                .text("desc comment 1").author(userForTest).build();
        item1.setComments(List.of(comment1));

        ItemDtoWithBookingAndComments result = ItemMapper.toItemDtoWithBookingAndComments(item1);

        assertEquals(item1.getId(), result.getId());
        assertEquals(item1.getDescription(), result.getDescription());
        assertEquals(item1.getAvailable(), result.getAvailable());
        assertEquals(item1.getComments().size(), result.getComments().size());
    }
}