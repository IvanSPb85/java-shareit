package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long counterId;

    @Override
    public Optional<User> save(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> update(User user) {
        users.put(user.getId(), user);
        return Optional.of(users.get(user.getId()));
    }

    @Override
    public Optional<User> getUser(long userId) {
        if (users.containsKey(userId)) {
            return Optional.of(users.get(userId));
        }
        return Optional.empty();
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public void removeUser(long userId) {
        users.remove(userId);
    }

    private long generateId() {
        return ++counterId;
    }
}
