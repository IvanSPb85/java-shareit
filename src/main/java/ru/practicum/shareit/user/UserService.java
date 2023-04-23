package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    UserDto findUser(long userId);

    Collection<UserDto> findAll();

    void deleteUser(long userId);
}
