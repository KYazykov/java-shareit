package src.main.java.ru.practicum.shareit.user;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserForResponseDto {

    private Long id;

    private String name;
}
