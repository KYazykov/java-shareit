package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;


@Data
@RequiredArgsConstructor
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private Boolean available = null;
    private long owner;
    private ItemRequest request = null;

    public ItemDto(int id, String name, String description, Boolean available, long owner, ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}

