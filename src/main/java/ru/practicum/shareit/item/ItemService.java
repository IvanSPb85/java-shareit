package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item create(Item item);

    Item update(Item item);

    Item findItemById(long itemId);

    Collection<Item> findItemsByOwner(long userId);

    Item findItemForRent(String itemName);
}
