package ru.practicum.shareit.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Data
@Component
public class UserServiceImp implements UserService {
    private final UserRepositoryJpa userRepositoryJpa;

    @Override
    public UserDto getUserById(Long id) {
        log.info("Получение пользователя по ID.");
        Optional<User> result = userRepositoryJpa.findById(id);
        if (result.isEmpty()) {
            String error = "В БД отсутствует запись о пользователе при получении пользователя по ID = " + id + ".";
            log.info(error);
            throw new ObjectNotFoundException(error);
        }
        return UserMapper.toUserDto(result.get());
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получение всех пользователей.");
        return userRepositoryJpa.findAll().stream()
                .map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        log.info("Добавление пользователя в БД.");
        User user = UserMapper.toUser(userDto);
        UserDto result = UserMapper.toUserDto(userRepositoryJpa.save(user));
        return result;
    }

    @Transactional
    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        userDto.setId(id);
        log.info("Обновление пользователя.");
        Optional<User> userForUpdate = userRepositoryJpa.findById(userDto.getId());
        if (userForUpdate.isPresent()) {
            if (userDto.getName() != null) {
                userForUpdate.get().setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                userForUpdate.get().setEmail(userDto.getEmail());
            }
            return UserMapper.toUserDto(userRepositoryJpa.save(userForUpdate.get()));
        } else {
            String error = "Ошибка при обновлении пользователя в БД. В БД отсутствует запись о пользователе с ID = "
                    + userDto.getId() + ".";
            throw new ObjectNotFoundException(error);
        }
    }

    @Override
    public Boolean deleteUser(Long id) {
        userRepositoryJpa.deleteById(id);
        return true;
    }
}
