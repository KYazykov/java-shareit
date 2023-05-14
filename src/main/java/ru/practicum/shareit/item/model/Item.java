package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class Item {

    private int id;
    @NotBlank(message = "Поле логина не может быть пустым.")
    @EqualsAndHashCode.Exclude
    private String name;

    @NotBlank(message = "Поле логина не может быть пустым.")
    @EqualsAndHashCode.Exclude
    private String description;
    private Boolean available = null;

    private long owner;
    private ItemRequest request;

    public Item(int id, String name, String description, Boolean available, long owner, ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}
