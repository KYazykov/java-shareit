package src.main.java.ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import src.main.java.ru.practicum.shareit.validation.CreateObject;
import src.main.java.ru.practicum.shareit.validation.UpdateObject;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    /**
     * Добавить юзера в БД.
     *
     * @param userDto пользователь.
     * @return добавляемый пользователь.
     */
    @PostMapping
    ResponseEntity<Object> addToStorage(@RequestBody @Validated(CreateObject.class) UserDto userDto) {
        return userClient.addToStorage(userDto);
    }

    /**
     * Обновить юзера в БД.
     *
     * @param userDto пользователь
     * @param userId  ID обновляемого пользователя.
     * @return обновлённый пользователь.
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateInStorage(@PathVariable long userId,
                                                  @Validated({UpdateObject.class}) @RequestBody UserDto userDto) {
        return userClient.updateInStorage(userDto, userId);
    }

    /**
     * Удалить пользователя из БД.
     *
     * @param userId ID удаляемого пользователя.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> removeFromStorage(@NumberFormat @PathVariable Long userId) {
        return userClient.removeFromStorage(userId);
    }

    /**
     * Получить список всех пользователей.
     *
     * @return список пользователей.
     */
    @GetMapping
    ResponseEntity<Object> getAllUsersFromStorage() {
        return userClient.getAllUsersFromStorage();
    }

    /**
     * Получить пользователя по ID.
     *
     * @param userId ID пользователя.
     * @return User - пользователь присутствует в библиотеке.
     * <p>null - пользователя нет в библиотеке.</p>
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@NotNull @PathVariable Long userId) {
        return userClient.getUserById(userId);
    }
}
