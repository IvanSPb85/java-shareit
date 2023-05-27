package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
import java.util.Optional;
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
        userService.findUser(userId);
        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(itemRequest -> addItemsToItemRequest(requests, itemRequest)).collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getAllRequestsPagination(long requestorId, Integer from, Integer size) {
        Collection<ItemRequest> requests;
        if (from != null && size != null) {
            if (from > 0 && size > 0) from = (from / size) + 1;
            requests = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(
                    requestorId, PageRequest.of(from, size));
        } else {
            requests = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(requestorId);
        }
        return requests.stream()
                .map(itemRequest -> addItemsToItemRequest(requests, itemRequest)).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        userService.findUser(userId);
        Optional<ItemRequest> requestOptional = itemRequestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            throw new InvalidParameterException(String.format("Запрос с id = %d не найден.", requestId));
        }
        Collection<Item> items = itemRepository.findAllByRequestIdIn(List.of(requestId));
        return ItemRequestMapper.toItemRequestDto(requestOptional.get(), items);
    }

    private ItemRequestDto addItemsToItemRequest(Collection<ItemRequest> requests, ItemRequest itemRequest) {
        List<Long> requestIdList = new ArrayList<>();
        requests.forEach(itemRequest1 -> requestIdList.add(itemRequest1.getId()));
        Collection<Item> items = itemRepository.findAllByRequestIdIn(requestIdList);

        List<Item> itemsByItemRequest = items.stream()
                .filter(item -> item.getRequestId() == itemRequest.getId()).collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemsByItemRequest);
    }
}
