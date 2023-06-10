package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Builder
@Data
public class BookingItemDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private User booker;
    private Item item;
}
