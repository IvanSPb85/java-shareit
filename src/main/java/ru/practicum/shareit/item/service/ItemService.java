package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto findItemById(long userId, long itemId);

    Collection<ItemDto> findItemsByOwner(long userId);

    Collection<ItemDto> findItemForRent(long userId, String itemName);
}
