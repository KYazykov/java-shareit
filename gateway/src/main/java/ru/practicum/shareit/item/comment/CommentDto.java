package ru.practicum.shareit.item.comment;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommentDto {
    private Long id;
    @JsonProperty("text")
    @NotNull(groups = {CreateObject.class}, message = "Описание запроса вещи не может быть null.")
    @NotBlank(groups = {CreateObject.class}, message = "Описание запроса вещи не может быть пустым.")
    private String text;
    private Long itemId;
    @JsonAlias({"authorName"})
    private String authorName;
    @JsonProperty("created")
    private LocalDateTime created;

}
