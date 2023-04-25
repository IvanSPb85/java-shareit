package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Optional<Item> save(Item item);

    Optional<Item> update(Item item);

    Optional<Item> getItemById(long itemId);

    Collection<Item> getItemByOwner(long userId);

    Collection<Item> getItemForRent(String itemName);
}
