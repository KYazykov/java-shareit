package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
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

    public Item(String name, String description, boolean available, Integer integer) {
    }
}
