package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long counterId;

    @Override
    public Optional<User> save(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return Optional.of(user);
    }

    @Override
    public Optional<User> update(User user) {
        emails.remove(users.get(user.getId()).getEmail());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
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
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public void removeUser(long userId) {
        emails.remove(getUser(userId).get().getEmail());
        users.remove(userId);
    }

    @Override
    public boolean isExistUser(long userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean isExistEmail(String email) {
        return emails.contains(email);
    }

    @Override
    public Collection<String> getAllEmails() {
        return emails;
    }

    private long generateId() {
        return ++counterId;
    }
}
