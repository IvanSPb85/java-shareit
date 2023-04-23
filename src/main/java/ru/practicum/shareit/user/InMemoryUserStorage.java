package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {

    @Override
    public Optional<User> save(User user) {
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User user) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findUser(long userId) {
        return Optional.empty();
    }

    @Override
    public Collection<User> findAll() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void removeUser(long userId) {
    }
}
