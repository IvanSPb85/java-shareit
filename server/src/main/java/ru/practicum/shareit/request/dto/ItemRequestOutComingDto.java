package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
public class ItemRequestOutComingDto {
    private long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private Collection<Item> items;
}
