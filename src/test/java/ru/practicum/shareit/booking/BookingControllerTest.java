package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.exception.BookingValidateException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepositoryJpa;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserForResponseDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingService bookingService;
    @MockBean
    ItemRepositoryJpa itemRepositoryJpa;
    @MockBean
    UserRepositoryJpa userRepositoryJpa;
    @MockBean
    BookingRepositoryJpa bookingRepositoryJpa;
    @Autowired
    MockMvc mockMvc;
    BookingDto bookingDtoForCreate;
    User owner1;
    User booker101;
    Item item1;
    LocalDateTime now;
    LocalDateTime nowPlus10Hours;
    LocalDateTime nowPlus20Hours;

    @BeforeEach
    void setup() {
        now = LocalDateTime.now();
        nowPlus10Hours = LocalDateTime.now().plusHours(10);
        nowPlus20Hours = LocalDateTime.now().plusHours(20);

        booker101 = User.builder()
                .id(101L)
                .name("имя юзера 101 booker")
                .email("booker@pochta.tu")
                .build();

        bookingDtoForCreate = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .booker(UserForResponseDto.builder().id(booker101.getId()).name(booker101.getName()).build())
                .startTime(nowPlus10Hours)
                .endTime(nowPlus20Hours)
                .bookingStatus(BookingStatus.WAITING)
                .build();

        owner1 = User.builder()
                .id(1L)
                .name("imya usera 1 owner")
                .email("owner1@m.ri")
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("nazvanie veschi 1")
                .description("opisanie veschi 1")
                .owner(owner1)
                .available(true)
                .build();

    }

    @DisplayName("Добавление букинга когда всё хорошо. ")
    @SneakyThrows
    @Test
    void add_whenAllIsOk_returnBookingForResponse() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .startTime(bookingDtoForCreate.getStartTime())
                .endTime(bookingDtoForCreate.getEndTime())
                .item(ItemMapper.toItemForResponseDto(item1))
                .booker(UserMapper.toUserOnlyWithIdDto(booker101))
                .status(BookingStatus.WAITING).build();

        when(bookingService.createBooking(any(), any())).thenReturn(bookingDto1ForResponse);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booker101.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoForCreate)))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto1ForResponse), result);
    }

    @SneakyThrows
    @Test
    void add_whenEndTimeBeforeStartTime_returnValidateException() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .startTime(now.plusDays(2))
                .endTime(now.plusDays(1))
                .item(ItemMapper.toItemForResponseDto(item1))
                .booker(UserMapper.toUserOnlyWithIdDto(booker101))
                .status(BookingStatus.WAITING).build();

        bookingDtoForCreate = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .booker(UserForResponseDto.builder().id(booker101.getId()).name(booker101.getName()).build())
                .startTime(bookingDto1ForResponse.getStartTime())
                .endTime(bookingDto1ForResponse.getEndTime())
                .bookingStatus(BookingStatus.WAITING)
                .build();

        Booking booking = Booking.builder()
                .id(bookingDtoForCreate.getId())
                .item(item1)
                .booker(booker101)
                .startTime(bookingDtoForCreate.getStartTime())
                .endTime(bookingDtoForCreate.getEndTime())
                .bookingStatus(bookingDtoForCreate.getBookingStatus())
                .build();

        item1.setBookings(List.of(booking));
        when(bookingService.createBooking(any(), any())).thenThrow(BookingValidateException.class);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booker101.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoForCreate)))

                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

    }

    @SneakyThrows
    @Test
    void updateByOwner() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .startTime(bookingDtoForCreate.getStartTime())
                .endTime(bookingDtoForCreate.getEndTime())
                .item(ItemMapper.toItemForResponseDto(item1))
                .booker(UserMapper.toUserOnlyWithIdDto(booker101))
                .status(BookingStatus.WAITING).build();

        when(bookingService.updateBooking(any(), any(), any()))
                .thenReturn(bookingDto1ForResponse);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingDto1ForResponse.getId())
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", owner1.getId())
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto1ForResponse), result);
    }

    @SneakyThrows
    @Test
    void getWithStatusById() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .startTime(bookingDtoForCreate.getStartTime())
                .endTime(bookingDtoForCreate.getEndTime())
                .item(ItemMapper.toItemForResponseDto(item1))
                .booker(UserMapper.toUserOnlyWithIdDto(booker101))
                .status(BookingStatus.WAITING).build();

        when(bookingService.getWithStatusById(any(), any()))
                .thenReturn(bookingDto1ForResponse);
        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingDto1ForResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker101.getId()))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingDto1ForResponse), result);
    }

    @SneakyThrows
    @Test
    void getByUserId() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .startTime(bookingDtoForCreate.getStartTime())
                .endTime(bookingDtoForCreate.getEndTime())
                .item(ItemMapper.toItemForResponseDto(item1))
                .booker(UserMapper.toUserOnlyWithIdDto(booker101))
                .status(BookingStatus.WAITING).build();
        when(bookingService.getByUserId(any(), any(), any(), any()))
                .thenReturn(List.of(bookingDto1ForResponse));

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker101.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto1ForResponse)), result);
    }

    @SneakyThrows
    @Test
    void getByOwnerId() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .startTime(bookingDtoForCreate.getStartTime())
                .endTime(bookingDtoForCreate.getEndTime())
                .item(ItemMapper.toItemForResponseDto(item1))
                .booker(UserMapper.toUserOnlyWithIdDto(booker101))
                .status(BookingStatus.WAITING).build();

        when(bookingService.getByOwnerId(any(), any(), any(), any()))
                .thenReturn(List.of(bookingDto1ForResponse));

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner1.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto1ForResponse)), result);
    }
}
