package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto item, long userId);

    ItemDto update(ItemDto item, long userId);

    ItemDto findItemById(long itemId);

    Collection<ItemDto> findItemsByOwner(long userId);

    Collection<ItemDto> findItemForRent(String itemName);
}
