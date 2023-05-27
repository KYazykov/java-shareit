package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    ItemDto itemDto;
    Item item;
    ItemDtoWithBookingAndComments itemWithBookingAndCommentsDto;
    User owner;
    User booker;
    @MockBean
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("name user")
                .email("mail@mall.nb")
                .build();

        booker = User.builder()
                .id(101L)
                .name("name booker")
                .email("booker@email.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("vesch №1")
                .description("opisanie veschi №1")
                .available(true)
                .requestId(1001L)
                .owner(owner)
                .build();

        itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();

        itemWithBookingAndCommentsDto = itemWithBookingAndCommentsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .requestId(item.getRequestId())
                .comments(new ArrayList<>())
                .build();

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @SneakyThrows
    @Test
    void testGetItemByIdForOwner() {
        when(itemService.getItemWithBookingAndComment(any(), any()))
                .thenReturn(itemWithBookingAndCommentsDto);

        mockMvc.perform(get("/items/{id}", itemDto.getId())
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(item.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testGetAll() {
        when(itemService.getItems(any()))
                .thenReturn(List.of(itemWithBookingAndCommentsDto));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(item.getDescription()), String.class))
                .andExpect(jsonPath("$[0].requestId", is(item.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testSearchItemsByText() {
        when(itemService.searchItems("found one item", 0, 10))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "found one item")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));

        when(itemService.searchItems("items not found", 0, 10))
                .thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", "items not found")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of())));
    }

    @SneakyThrows
    @Test
    void testAdd() {
        when(itemService.addItem(any(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(item.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testUpdate_whenAllAreOk_aAndReturnUpdatedItem() {
        when(itemService.updateItem(any(), any(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testUpdate_whenAllAreNotOk_aAndReturnExceptionNotFoundRecordInBD() {
        when(itemService.updateItem(any(), any(), any()))
                .thenThrow(ObjectNotFoundException.class);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void addCommentToItem_whenAllIsOk_returnSavedComment() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("comment 1")
                .authorName("name user")
                .created(LocalDateTime.now().minusSeconds(5)).build();
        when(itemService.saveComment(any(), any(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class));
    }
}
