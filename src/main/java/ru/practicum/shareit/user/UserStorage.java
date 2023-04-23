package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Optional<User> save(User user);

    Optional<User> update(User user);

    Optional<User> getUser(long userId);

    Collection<User> getAll();

    void removeUser(long userId);
}
