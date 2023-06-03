package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.DataBaseException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserDto userDto = new UserDto(1L, "user", "user@mail.ru");

    @Test
    void create_whenDuplicateEmail_thenDataBaseExceptionThrown() {
        User user = UserMapper.toUser(userDto);
        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        DataBaseException dataBaseException = assertThrows(DataBaseException.class,
                () -> userService.create(userDto));

        assertEquals("Пользователь с email = user@mail.ru уже существует.",
                dataBaseException.getMessage());
        verify(userRepository).save(user);
    }

    @Test
    void create_whenUniqueEmail_thenReturnedUser() {
        User expectedUser = new User(1L, "user", "user@mail.ru");
        User user = UserMapper.toUser(userDto);
        when(userRepository.save(user)).thenReturn(expectedUser);

        UserDto actualUser = userService.create(userDto);

        assertEquals(UserMapper.toUserDto(expectedUser), actualUser);
        verify(userRepository).save(UserMapper.toUser(userDto));
    }

    @Test
    void updateUser_whenExistEmail_thenDataBaseExceptionThrown() {
        UserDto updateUser = new UserDto(1L, "updateUser", "updateUser@mail.ru");
        User foundUser = new User(1L, "user", "user@mail.ru");
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(foundUser));
        when(userRepository.existsByEmail(updateUser.getEmail())).thenReturn(true);

        DataBaseException dataBaseException = assertThrows(DataBaseException.class,
                () -> userService.updateUser(1L, updateUser));

        assertEquals("Невозможно обновить пользователя с email = updateUser@mail.ru," +
                " т.к. уже существует другой пользователь с таким email", dataBaseException.getMessage());
    }

    @Test
    void updateUser_whenExistOwnEmail_thenReturnedUpdatedUser() {
        UserDto updateUser = new UserDto(1L, "updateUser", "updateUser@mail.ru");
        User user = UserMapper.toUser(updateUser);
        User foundUser = new User(1L, "user", "updateUser@mail.ru");
        when(userRepository.findById(updateUser.getId()))
                .thenReturn(Optional.of(foundUser));
        when(userRepository.existsByEmail(updateUser.getEmail())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        UserDto actualUser = userService.updateUser(1L, updateUser);

        assertEquals(updateUser, actualUser);
        verify(userRepository).existsByEmail(updateUser.getEmail());
        verify(userRepository).save(user);
        verify(userRepository).findById(updateUser.getId());
    }

    @Test
    void updateUser_whenNotExistEmail_thenReturnedUpdatedUser() {
        UserDto updateUser = new UserDto(1L, "updateUser", "updateUser@mail.ru");
        User user = UserMapper.toUser(updateUser);
        User foundUser = new User(1L, "user", "user@mail.ru");
        when(userRepository.findById(updateUser.getId()))
                .thenReturn(Optional.of(foundUser));
        when(userRepository.existsByEmail(updateUser.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        UserDto actualUser = userService.updateUser(1L, updateUser);

        assertEquals(updateUser, actualUser);
        verify(userRepository).existsByEmail(updateUser.getEmail());
        verify(userRepository).save(user);
        verify(userRepository).findById(updateUser.getId());
    }

    @Test
    void findUser_whenNotFound_thenInvalidParameterExceptionThrown() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> userService.findUser(1L));

        assertEquals("Пользователь с id = 1 не найден.", exception.getMessage());
    }

    @Test
    void findUser_whenFound_thenReturnedUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(UserMapper.toUser(userDto)));

        UserDto actualUser = userService.findUser(1L);

        assertEquals(userDto, actualUser);
    }

    @Test
    void findAll() {
        when(userRepository.findAll()).thenReturn(List.of(UserMapper.toUser(userDto)));

        Collection<UserDto> users = userService.findAll();

        assertEquals(1, users.size());
        assertTrue(users.contains(userDto));
    }

    @Test
    void deleteUser_whenUserNotFound_thenInvalidParameterExceptionThrown() {
        when(userRepository.existsById(userDto.getId())).thenReturn(false);

        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> userService.deleteUser(userDto.getId()));

        assertEquals("Удаление пользователя с id = 1 неосуществимо, т.к. данный пользователь не найден.",
                exception.getMessage());
    }

    @Test
    void deleteUser_whenUserFound_thenDeleteUser() {
        when(userRepository.existsById(userDto.getId())).thenReturn(true);

        userService.deleteUser(userDto.getId());

        verify(userRepository).deleteById(userDto.getId());
    }
}