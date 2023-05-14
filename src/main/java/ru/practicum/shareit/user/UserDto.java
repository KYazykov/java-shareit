package ru.practicum.shareit.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;

@Data
@RequiredArgsConstructor
public class UserDto {

    private Long id;
    private HashMap<Integer, Item> userItems = new HashMap<>();

    @Email(message = "Почта не соответствует формату.")
    @NotBlank(message = "Поле имени не может быть пустым.")
    @EqualsAndHashCode.Exclude
    private String email;

    @NotBlank(message = "Поле имени не может быть пустым.")
    @EqualsAndHashCode.Exclude
    private String name;

    public UserDto(Long id, HashMap<Integer, Item> userItems, String email, String name) {
        this.id = id;
        this.userItems = userItems;
        this.email = email;
        this.name = name;
    }
}
