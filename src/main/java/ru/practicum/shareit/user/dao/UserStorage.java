package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Optional<User> save(User user);

    Optional<User> update(User user);

    Optional<User> getUser(long userId);

    Collection<User> getAll();

    void removeUser(long userId);
}
