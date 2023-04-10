package ru.practicum.shareit.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@RequiredArgsConstructor
public class User {
    private Long id;
    private HashMap<Integer,Item> userItems = new HashMap<>();

    @Email(message = "Почта не соответствует формату.")
    @NotBlank(message = "Поле имени не может быть пустым.")
    @EqualsAndHashCode.Exclude
    private String email;

    @NotBlank(message = "Поле имени не может быть пустым.")
    @EqualsAndHashCode.Exclude
    private String name;

}
