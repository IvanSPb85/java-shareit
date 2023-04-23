package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    User findUser(long userId);

    List<User> findAll();

    void removeUser(long userId);
}
