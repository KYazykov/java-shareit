package ru.practicum.shareit.user;


import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    Boolean deleteUser(Long id);

}
