package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataBaseException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User saveUser;
        try {
            saveUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.warn("Пользователь с email = {} уже существует.", user.getEmail());
            throw new DataBaseException(String.format("Пользователь с email = %s уже существует.",
                    user.getEmail()));
        }
        log.info("Пользователь с id = {} и email = {} успешно создан.",
                saveUser.getId(), saveUser.getEmail());
        return UserMapper.toUserDto(saveUser);
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        User updatingUser = UserMapper.toUser(findUser(userId));
        User user = UserMapper.toUser(userDto);
        if (user.getEmail() != null) {
            if (userRepository.existsByEmail(user.getEmail()) && !user.getEmail().equals(updatingUser.getEmail())) {
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
        User saveUser = userRepository.save(updatingUser);
        log.info("Пользователь с id = {} успешно обновлен.", saveUser.getId());
        return UserMapper.toUserDto(saveUser);
    }

    public UserDto findUser(long userId) {
        Optional<User> result = userRepository.findById(userId);
        if (result.isEmpty()) {
            log.warn("Пользователь с id = {} не найден.", userId);
            throw new InvalidParameterException(String.format("Пользователь с id = %d не найден.", userId));
        }
        log.info("Пользователь с id = {} найден.", result.get().getId());
        return UserMapper.toUserDto(result.get());

    }

    public Collection<UserDto> findAll() {
        Collection<User> users = userRepository.findAll();
        log.info("Найдено {} пользователей.", users.size());
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public void deleteUser(long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            log.info("Пользователь с id = {} успешно удален.", userId);
        } else {
            log.warn("Удаление пользователя с id = {} неосуществимо, т.к. данный пользователь не найден.", userId);
            throw new InvalidParameterException(String.format(
                    "Удаление пользователя с id = %d неосуществимо, т.к. данный пользователь не найден.", userId));
        }
    }
}
