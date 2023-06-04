package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestIncomingDto itemRequestIncomingDto, User requestor) {
        return ItemRequest.builder()
                .id(itemRequestIncomingDto.getId())
                .description(itemRequestIncomingDto.getDescription())
                .requestor(requestor)
                .created(itemRequestIncomingDto.getCreated()).build();
    }

    public static ItemRequestOutComingDto toItemRequestOutcomingDto(ItemRequest itemRequest, Collection<Item> items) {
        return ItemRequestOutComingDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .items(items).build();
    }
}
