package ru.practicum.shareit.user;


import java.util.List;

public interface UserStorage {
    List<User> getUsers();
    User getUser(Long id);
    User addUser(User user);
    User updateUser(Long id, User user);
    Boolean deleteUser(Long id);

}
