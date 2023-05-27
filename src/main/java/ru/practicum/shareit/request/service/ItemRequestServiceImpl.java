package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        User user;
        try {
            user = UserMapper.toUser(userService.findUser(userId));
        } catch (InvalidParameterException e) {
            log.warn("Нельзя создать запрос от несуществующего пользователя с id = {}", userId);
            throw new InvalidParameterException(
                    String.format("Нельзя создать запрос от несуществующего пользователя с id = %d", userId));
        }
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        ItemRequest saveItemRequest = itemRequestRepository.save(itemRequest);
        log.info("Запрос c id = {} успешно сохранен в базе данных.", saveItemRequest.getId());
        return ItemRequestMapper.toItemRequestDto(saveItemRequest, Collections.emptyList());
    }

    @Override
    public Collection<ItemRequestDto> getOwnRequests(long userId) {
        User user = UserMapper.toUser(userService.findUser(userId));
        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        List<Long> requestIdList = new ArrayList<>();
        requests.forEach(itemRequest -> requestIdList.add(itemRequest.getId()));
        Collection<Item> items = itemRepository.findAllByRequestIdIn(requestIdList);
        return requests.stream()
                .map(itemRequest -> addItemsToItemRequest(itemRequest, items)).collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getAllRequests(long userId, Integer from, Integer size) {
        return null;
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        return null;
    }

    private ItemRequestDto addItemsToItemRequest(ItemRequest itemRequest, Collection<Item> items) {
        List<Item> itemsByItemRequest = items.stream()
                .filter(item -> item.getRequestId() == itemRequest.getId()).collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemsByItemRequest);
    }
}
