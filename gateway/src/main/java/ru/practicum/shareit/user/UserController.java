package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.CreateObject;
import ru.practicum.shareit.validation.UpdateObject;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    ResponseEntity<Object> addToStorage(@RequestBody @Validated(CreateObject.class) UserDto userDto) {
        return userClient.addToStorage(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateInStorage(@PathVariable long userId,
                                                  @Validated({UpdateObject.class}) @RequestBody UserDto userDto) {
        return userClient.updateInStorage(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> removeFromStorage(@NumberFormat @PathVariable Long userId) {
        return userClient.removeFromStorage(userId);
    }

    @GetMapping
    ResponseEntity<Object> getAllUsersFromStorage() {
        return userClient.getAllUsersFromStorage();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@NotNull @PathVariable Long userId) {
        return userClient.getUserById(userId);
    }
}
