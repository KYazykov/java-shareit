package ru.practicum.shareit.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;

/**
 * TODO Sprint add-controllers.
 */
@Data
@RequiredArgsConstructor
public class User {
    private Long id;
    private HashMap<Integer, Item> userItems = new HashMap<>();

    @Email(message = "Почта не соответствует формату.")
    @NotBlank(message = "Поле имени не может быть пустым.")
    @EqualsAndHashCode.Exclude
    private String email;

    @NotBlank(message = "Поле имени не может быть пустым.")
    @EqualsAndHashCode.Exclude
    private String name;

}
