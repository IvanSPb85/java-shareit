package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto create(UserDto userDto) {
        return null;
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        return null;
    }

    public UserDto findUser(long userId) {
        return null;
    }

    public Collection<UserDto> findAll() {
        return null;
    }

    public void deleteUser(long userId) {

    }
}
