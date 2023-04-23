package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.DataBaseException;

import java.security.InvalidParameterException;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long userId;

    @Override
    public Optional<User> save(User user) {
        checkEmail(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new InvalidParameterException(String.format("Пользователь с id = %s не найден.", user.getId()));
        }
        User existingUser = users.get(user.getId());
        if (user.getEmail() != null) {
            checkEmail(user);
            existingUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        return Optional.of(users.get(user.getId()));
    }

    @Override
    public Optional<User> findUser(long userId) {
        if (users.containsKey(userId)) {
            return Optional.of(users.get(userId));
        }
        throw new InvalidParameterException(String.format("Пользователь с id = %s не найден.", userId));
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public void removeUser(long userId) {
        findUser(userId);
        users.remove(userId);
    }

    private long generateId() {
        return ++userId;
    }

    private void checkEmail(User user) {
        users.forEach((aLong, user1) -> {
            if (user1.getEmail().equals(user.getEmail()) && user.getId() != aLong) {
                throw new DataBaseException(String.format("Пользователь с email = %s уже существует.",
                        user.getEmail()));
            }
        });
    }
}
