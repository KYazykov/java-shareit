package src.main.java.ru.practicum.shareit.item.comment;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommentDto {
    private Long id;
    @JsonProperty("text")
    private String text;
    private Long itemId;
    @JsonAlias({"authorName"})
    private String authorName;
    @JsonProperty("created")
    private LocalDateTime created;

}
