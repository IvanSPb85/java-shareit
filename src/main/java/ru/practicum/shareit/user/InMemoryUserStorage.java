package ru.practicum.shareit.user;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    @Override
    public Optional<User> save(User user) {
        return null;
    }

    @Override
    public Optional<User> update(User user) {
        return null;
    }

    @Override
    public Optional<User> findUser(long userId) {
        return null;
    }

    @Override
    public Collection<User> findAll() {
        return null;
    }

    @Override
    public void removeUser(long userId) {

    }
}
