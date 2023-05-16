package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.ShortBookingItemDto;
import ru.practicum.shareit.booking.model.Booking;


@Builder
@Data
public class ItemBookingsDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private ShortBookingItemDto lastBooking;
    private ShortBookingItemDto nextBooking;
}
