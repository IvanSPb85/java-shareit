package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.DataBaseException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        Optional<User> result = userStorage.save(user);
        if (result.isPresent()) {
            User savedUser = result.get();
            log.info("Пользователь с id = {} успешно создан.", result.get().getId());
            return UserMapper.toUserDto(savedUser);
        }
        throw new DataBaseException("Ошибка базы данных при создании пользователя.");
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(userId);
        Optional<User> result = userStorage.update(user);
        if (result.isPresent()) {
            User updatedUser = result.get();
            log.info("Пользователь с id = {} успешно обновлен.", result.get().getId());
            return UserMapper.toUserDto(updatedUser);
        }
        throw new DataBaseException("Ошибка базы данных при обновлении пользователя.");
    }

    public UserDto findUser(long userId) {
        Optional<User> result = userStorage.findUser(userId);
        if (result.isPresent()) {
            log.info("Пользователь с id = {} найден.", result.get().getId());
            return UserMapper.toUserDto(result.get());
        }
        throw new DataBaseException(String.format("Ошибка базы данных при поиске пользователя c id = %s.", userId));
    }

    public Collection<UserDto> findAll() {
        Collection<User> users = userStorage.findAll();
        log.info("Найдено {} пользователей.", users.size());
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public void deleteUser(long userId) {
        findUser(userId);
        userStorage.removeUser(userId);
        log.info("Пользователь с id = {} успешно удален.", userId);
    }
}
