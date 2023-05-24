package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    @Override
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        return null;
    }

    @Override
    public Collection<ItemRequestDto> getOwnRequests(long userId) {
        return null;
    }

    @Override
    public Collection<ItemRequestDto> getAllRequests(long userId, Integer from, Integer size) {
        return null;
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        return null;
    }
}
