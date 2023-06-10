package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestIncomingDto;
import ru.practicum.shareit.request.dto.ItemRequestOutComingDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestOutComingDto create(long userId, ItemRequestIncomingDto itemRequestIncomingDto);

    Collection<ItemRequestOutComingDto> getOwnRequests(long userId);

    Collection<ItemRequestOutComingDto> getAllRequestsPagination(long requestorId, Integer from, Integer size);

    ItemRequestOutComingDto getRequestById(long userId, long requestId);
}
