package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null) // todo
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.isAvailable())
//                .request() // todo
                .build();
    }
}