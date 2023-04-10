package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Collection;


/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    UserDto userDto = new UserDto();

    @GetMapping
    public Collection<User> getUsers() {
        return userDto.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userDto.getUser(id);
    }
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userDto.addUser(user);
    }
    @PatchMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userDto.updateUser(id, user);
    }
    @DeleteMapping ("/{id}")
    public Boolean deleteUser(@PathVariable Long id) {
        return userDto.deleteUser(id);
    }
}
