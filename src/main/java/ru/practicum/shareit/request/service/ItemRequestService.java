package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> getOwnRequests(long userId);

    Collection<ItemRequestDto> getAllRequests(long userId);

    ItemRequestDto getRequestById(long userId);
}
