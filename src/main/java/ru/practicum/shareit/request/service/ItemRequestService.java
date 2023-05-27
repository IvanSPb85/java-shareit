package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> getOwnRequests(long userId);

    Collection<ItemRequestDto> getAllRequestsPagination(long requestorId, Integer from, Integer size);

    ItemRequestDto getRequestById(long userId, long requestId);
}
