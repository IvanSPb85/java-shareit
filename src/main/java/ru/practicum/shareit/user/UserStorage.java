package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> save(User user);

    Optional<User> update(User user);

    Optional<User> findUser(long userId);

    Collection<User> findAll();

    void removeUser(long userId);
}
