package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserForResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @InjectMocks
    ItemRequestController itemRequestController;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;
    ItemDto itemDto;
    User owner;
    User booker;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;
    ItemRequestDtoWithAnswers itemRequestDtoWithAnswers;

    BookingDto bookingDtoForCreate;
    User owner1;
    User booker101;
    User requester51;
    UserForResponseDto requesterDto51;
    Item item1;
    LocalDateTime now;
    LocalDateTime nowPlus10Hours;
    LocalDateTime nowPlus20Hours;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        nowPlus10Hours = now.plusHours(10);
        nowPlus20Hours = now.plusHours(20);

        owner1 = User.builder()
                .id(1L)
                .name("imya usera 1 owner")
                .email("owner1@m.ri")
                .build();

        booker101 = User.builder()
                .id(101L)
                .name("imya usera 101 booker")
                .email("booker@pochta.tu")
                .build();

        requester51 = User.builder()
                .id(51L)
                .name("name requester")
                .email("requester@yaschik.po")
                .build();
        requesterDto51 = UserForResponseDto.builder()
                .id(requester51.getId())
                .name(requester51.getName())
                .build();

        assertEquals(requester51.getId(), requesterDto51.getId());
        assertEquals(requester51.getName(), requesterDto51.getName());

        item1 = Item.builder()
                .id(1L)
                .name("nazvanie veschi 1")
                .description("opisanie veschi 1")
                .owner(owner1)
                .available(true)
                .requestId(1L)
                .bookings(List.of())
                .comments(List.of())
                .build();
        itemDto = ItemMapper.toItemDto(item1);

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Book")
                .requester(requester51)
                .created(now)
                .items(List.of(item1))
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(requesterDto51)
                .build();

        bookingDtoForCreate = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .booker(UserForResponseDto.builder().id(booker101.getId()).name(booker101.getName()).build())
                .startTime(nowPlus10Hours)
                .endTime(nowPlus20Hours)
                .bookingStatus(BookingStatus.WAITING)
                .build();

    }

    @SneakyThrows
    @Test
    void addItemRequest() {
        when(itemRequestService.addItemRequest(any(), any()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", itemRequest.getRequester().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requester.id", is(itemRequestDto.getRequester().getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void getItemRequestsByUserId() {
        ItemRequestDtoWithAnswers itemRequestDtoWithAnswersForOutput = ItemRequestDtoWithAnswers.builder()
                .id(1L)
                .description("Book")
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.getItemRequestsByUserId(any()))
                .thenReturn(List.of(itemRequestDtoWithAnswersForOutput));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(List.of(itemRequestDtoWithAnswersForOutput))));
        verify(itemRequestService, times(1)).getItemRequestsByUserId(1L);


    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        itemRequestDtoWithAnswers = ItemRequestDtoWithAnswers.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(null)
                .created(now)
                .build();

        when(itemRequestService.getAllRequestForSee(any(), any(), any()))
                .thenReturn(List.of(itemRequestDtoWithAnswers));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(List.of(itemRequestDtoWithAnswers))));
        verify(itemRequestService, times(1)).getAllRequestForSee(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        itemRequestDtoWithAnswers = ItemRequestDtoWithAnswers.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(null)
                .created(now)
                .build();

        when(itemRequestService.getItemRequestById(any(), any()))
                .thenReturn(itemRequestDtoWithAnswers);

        mockMvc.perform(get("/requests/{requestId}", itemRequestDtoWithAnswers.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(itemRequestDtoWithAnswers)));
        verify(itemRequestService, times(1)).getItemRequestById(any(), any());
    }
}
