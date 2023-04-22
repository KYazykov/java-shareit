package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.List;


@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userStorageImp") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers();
    }

    public UserDto getUser(Long id) {
        if (UserStorageImp.users.containsKey(id)) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        return userStorage.getUser(id);
    }
}
