package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private Long requestId;
}