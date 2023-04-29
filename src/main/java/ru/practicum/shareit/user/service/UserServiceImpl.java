package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataBaseException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (userStorage.isExistEmail(user.getEmail())) {
            throw new DataBaseException(String.format("Пользователь с email = %s уже существует.",
                    user.getEmail()));
        }
        Optional<User> result = userStorage.save(user);
        if (result.isPresent()) {
            log.info("Пользователь с id = {} и email = {} успешно создан.",
                    result.get().getId(), result.get().getEmail());
            return UserMapper.toUserDto(result.get());
        }
        throw new DataBaseException("Ошибка базы данных при создании пользователя.");
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        User updatingUser = UserMapper.toUser(findUser(userId));
        User user = UserMapper.toUser(userDto);
        if (user.getEmail() != null) {
            Collection<String> checkEmails = userStorage.getAllEmails();
            checkEmails.remove(userStorage.getUser(userId).get().getEmail());
            if (userStorage.isExistEmail(user.getEmail())) {
                log.warn("Невозможно обновить пользователя с email = {}, т.к. уже существует другой пользователь с " +
                        "таким email", user.getEmail());
                throw new DataBaseException(String.format("Невозможно обновить пользователя с email = %s," +
                        " т.к. уже существует другой пользователь с таким email", user.getEmail()));
            }
            updatingUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updatingUser.setName(user.getName());
        }
        Optional<User> result = userStorage.update(updatingUser);
        if (result.isPresent()) {
            log.info("Пользователь с id = {} успешно обновлен.", result.get().getId());
            return UserMapper.toUserDto(result.get());
        }
        throw new DataBaseException("Ошибка базы данных при обновлении пользователя.");
    }

    public UserDto findUser(long userId) {
        Optional<User> result = userStorage.getUser(userId);
        if (result.isEmpty()) {
            throw new InvalidParameterException(String.format("Пользователь с id = %d не найден.", userId));
        }
        log.info("Пользователь с id = {} найден.", result.get().getId());
        return UserMapper.toUserDto(result.get());

    }

    public Collection<UserDto> findAll() {
        Collection<User> users = userStorage.getAllUsers();
        log.info("Найдено {} пользователей.", users.size());
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public void deleteUser(long userId) {
        if (userStorage.isExistUser(userId)) {
            userStorage.removeUser(userId);
            log.info("Пользователь с id = {} успешно удален.", userId);
        } else {
            log.warn("Удаление пользователя с id = {} неосуществимо, т.к. данный пользователь не найден.", userId);
            throw new InvalidParameterException(String.format(
                    "Удаление пользователя с id = %d неосуществимо, т.к. данный пользователь не найден.", userId));
        }
    }
}
