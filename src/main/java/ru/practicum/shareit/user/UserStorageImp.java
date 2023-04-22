package ru.practicum.shareit.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailValidationException;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Data
@Component
public class UserStorageImp implements UserStorage {
    public static final HashMap<Long, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public List<UserDto> getUsers() {
        log.info("Получен запрос на получение списка пользователей: {}", users.values());
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : users.values()) {
            userDtoList.add(UserMapper.toUserDto(user));
        }
        return userDtoList;
    }

    @Override
    public UserDto getUser(Long id) {
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        return UserMapper.toUserDto(users.get(id));
    }

    @Override
    public UserDto addUser(User user) {
        if (users.containsKey(user.getId()) || users.containsValue(user)) {
            throw new ObjectNotFoundException("Такой пользователь уже есть");
        }
        for (User userOne : users.values()) {
            if (userOne.getEmail().equals(user.getEmail())) {
                throw new EmailValidationException("Такой емэйл уже занят");
            }
        }
        user.setId((long) id++);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен email: {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long id, User user) {
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("Такого пользователя нет");
        }
        if (user.getEmail() == null) {
            user.setEmail(getUser(id).getEmail());
        }
        if (user.getName() == null) {
            user.setName(getUser(id).getName());
        }
        for (User userOne : users.values()) {
            if (!Objects.equals(userOne.getId(), id)) {
                if (userOne.getEmail().equals(user.getEmail())) {
                    throw new EmailValidationException("Такой емэйл уже занят");
                }
            }
        }
        user.setId(id);
        users.put(user.getId(), user);
        log.info("Пользователь обнавлен email: {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public Boolean deleteUser(Long id) {
        users.remove(id);
        return true;
    }
}
