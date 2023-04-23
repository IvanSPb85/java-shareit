package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.DataBaseException;
import ru.practicum.shareit.user.dto.UserDto;

import java.security.InvalidParameterException;
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
        checkEmail(user);
        Optional<User> result = userStorage.save(user);
        if (result.isPresent()) {
            log.info("Пользователь с id = {} успешно создан.", result.get().getId());
            return UserMapper.toUserDto(result.get());
        }
        throw new DataBaseException("Ошибка базы данных при создании пользователя.");
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        User updatingUser = UserMapper.toUser(findUser(userId));
        User user = UserMapper.toUser(userDto);
        if (user.getEmail() != null) {
            checkEmail(user);
            updatingUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updatingUser.setName(user.getName());
        }
        Optional<User> result = userStorage.update(updatingUser);
        if (result.isPresent()) {
            User updatedUser = result.get();
            log.info("Пользователь с id = {} успешно обновлен.", updatedUser.getId());
            return UserMapper.toUserDto(updatedUser);
        }
        throw new DataBaseException("Ошибка базы данных при обновлении пользователя.");
    }

    public UserDto findUser(long userId) {
        Optional<User> result = userStorage.getUser(userId);
        if (result.isEmpty()) {
            throw new InvalidParameterException(String.format("Пользователь с id = %s не найден.", userId));
        }
        log.info("Пользователь с id = {} найден.", result.get().getId());
        return UserMapper.toUserDto(result.get());

    }

    public Collection<UserDto> findAll() {
        Collection<User> users = userStorage.getAll();
        log.info("Найдено {} пользователей.", users.size());
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public void deleteUser(long userId) {
        findUser(userId);
        userStorage.removeUser(userId);
        log.info("Пользователь с id = {} успешно удален.", userId);
    }

    private void checkEmail(User user) {
        userStorage.getAll().forEach(user1 -> {
            if (user1.getEmail().equals(user.getEmail()) && user1.getId() != user.getId()) {
                throw new DataBaseException(String.format("Пользователь с email = %s уже существует.",
                        user.getEmail()));
            }
        });
    }
}
