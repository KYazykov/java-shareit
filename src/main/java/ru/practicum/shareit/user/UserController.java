package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    UserStorageImp userStorageImp = new UserStorageImp();

    @GetMapping
    public Collection<UserDto> getUsers() {
        return userStorageImp.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userStorageImp.getUser(id);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody UserDto userDto) {
        return userStorageImp.addUser(UserStorageImp.toUser(userDto));
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userStorageImp.updateUser(id, UserStorageImp.toUser(userDto));
    }

    @DeleteMapping("/{id}")
    public Boolean deleteUser(@PathVariable Long id) {
        return userStorageImp.deleteUser(id);
    }
}
