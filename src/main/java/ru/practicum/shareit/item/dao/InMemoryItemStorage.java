package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long counterId;

    @Override
    public Optional<Item> save(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> update(Item item) {
        items.put(item.getId(), item);
        return Optional.of(items.get(item.getId()));
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        if (items.containsKey(itemId)) {
            return Optional.of(items.get(itemId));
        }
        return Optional.empty();
    }

    @Override
    public Collection<Item> getItemByOwner(long userId) {
        return items.values().stream().filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getItemForRent(String itemName) {
        String itemNameLowerCase = itemName.toLowerCase();
        Collection<Item> availableItems = new ArrayList<>();
        items.values().forEach(item -> {
            if ((item.getName().toLowerCase().contains(itemNameLowerCase)
                    || item.getDescription().toLowerCase().contains(itemNameLowerCase))
                    && item.isAvailable()) {
                availableItems.add(item);
            }
        });
        return availableItems;
    }

    private long generateId() {
        return ++counterId;
    }

}
