package ru.practicum.shareit.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@Data
@RequiredArgsConstructor
@Slf4j
public class UserDto implements UserStorage{

    private int id = 1;
    private static HashMap<Long,User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        log.info("Получен запрос на получение списка пользователей: {}", users.values());

        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long id) {
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        if (users.containsKey(user.getId()) || users.containsValue(user)) {
            throw new ObjectNotFoundException("Такой пользователь уже есть");
        }
        for (User userOne: users.values()) {
            if(userOne.getEmail().equals(user.getEmail())) {
                throw new ObjectNotFoundException("Такой емэйл уже занят");
            }
        }
        user.setId((long) id++);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен email: {}", user);
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("Такого пользователя нет");
        }
        if (user.getEmail() == null) {
            user.setEmail(getUser(id).getEmail());
        }
        if (user.getName() == null) {
            user.setName(getUser(id).getName());
        }
        for (User userOne: users.values()) {
            if (userOne.getId() != id) {
                if (userOne.getEmail().equals(user.getEmail())) {
                    throw new ObjectNotFoundException("Такой емэйл уже занят");
                }
            }
        }
        user.setId(id);
        users.put(user.getId(), user);
        log.info("Пользователь обнавлен email: {}", user);
        return user;
    }
    @Override
    public Boolean deleteUser(Long id) {
        users.remove(id);
        return true;
    }

    private String name;
    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail());
    };
}
