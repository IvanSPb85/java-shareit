package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.InComingCommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutComingCommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemBookingsDto findItemById(long userId, long itemId);

    Collection<ItemBookingsDto> findItemsByOwner(long userId);

    Collection<ItemDto> findItemForRent(long userId, String itemName);

    Item findItem(long itemId);

    OutComingCommentDto createComment(long userId, long itemId, InComingCommentDto inComingCommentDto);
}
