package ru.practicum.shareit.user;


import java.util.List;

public interface UserStorage {
    List<UserDto> getUsers();

    UserDto getUser(Long id);

    UserDto addUser(User user);

    UserDto updateUser(Long id, User user);

    Boolean deleteUser(Long id);

}
