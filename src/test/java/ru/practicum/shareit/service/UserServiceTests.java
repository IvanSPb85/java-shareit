package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.DataBaseException;
import ru.practicum.shareit.user.dao.InMemoryUserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.security.InvalidParameterException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTests {
    private UserService service;
    private final UserDto testUserDto = new UserDto("TestUser", "TestUser@email");

    @BeforeEach
    public void createNewUserService() {
        service = new UserServiceImpl(new InMemoryUserStorage());
    }

    @Test
    public void createNewUserTest() {
        assertEquals(service.findAll().size(), 0, "В базе не должно быть пользователей.");
        UserDto userDto = service.create(testUserDto);
        assertEquals(service.findAll().size(), 1, "В базе должен быть один пользователь.");
        assertEquals(userDto.getId(), 1, "id must be 1.");
        assertEquals(userDto.getName(), "TestUser", "name must be TestUSer.");
        assertEquals(userDto.getEmail(), "TestUser@email", "email must be TestUser@email");
    }

    @Test
    public void createFailIdUserTest() {
        UserDto userDto = service.create(new UserDto(100L, "FailId", "User@mail"));
        assertEquals(userDto.getId(), 1, "id must be 1.");
    }

    @Test
    public void createDuplicateEmailUserTest() {
        service.create(testUserDto);
        DataBaseException exception = assertThrows(DataBaseException.class,
                () -> service.create(testUserDto));
        assertEquals(exception.getMessage(), String.format("Пользователь с email = %s уже существует.",
                testUserDto.getEmail()));
    }

    @Test
    public void updateUserTest() {
        UserDto userDto = service.create(testUserDto);
        UserDto updatedUser = service.updateUser(1L, new UserDto(1L, "UpdateName", "new@mail"));
        assertEquals(updatedUser.getEmail(), "new@mail");
        assertEquals(updatedUser.getName(), "UpdateName");
    }

    @Test
    public void updateDuplicateEmailTEst() {
        UserDto userDto = service.create(testUserDto);
        UserDto secondUser = service.create(new UserDto("secondUser", "User@mail"));
        DataBaseException exception = assertThrows(DataBaseException.class,
                () -> service.updateUser(1L, new UserDto("name", "User@mail")));
        assertEquals(exception.getMessage(), String.format("Невозможно обновить пользователя с email = %s," +
                        " т.к. уже существует другой пользователь с таким email",
                secondUser.getEmail()));
    }

    @Test
    public void findUserTest() {
        UserDto user = service.create(testUserDto);
        assertEquals(service.findUser(1L), user);
    }

    @Test
    public void deleteTest() {
        UserDto user = service.create(testUserDto);
        service.deleteUser(1L);
        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> service.findUser(1L));
        assertEquals(exception.getMessage(), String.format("Пользователь с id = %d не найден.", 1));
    }

    @Test
    public void findAllTest() {
        UserDto user = service.create(testUserDto);
        UserDto secondUser = service.create(new UserDto("name", "email@email"));
        List<UserDto> users = List.of(user, secondUser);
        assertEquals(service.findAll().size(), 2);
        assertEquals(service.findAll(), users);
    }

    @Test
    public void deleteFailIdUser() {
        UserDto user = service.create(testUserDto);
        assertEquals(service.findAll().size(), 1);
        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> service.deleteUser(2L));
        assertEquals(exception.getMessage(), String.format(
                "Удаление пользователя с id = %d неосуществимо, т.к. данный пользователь не найден.", 2));
    }
}
