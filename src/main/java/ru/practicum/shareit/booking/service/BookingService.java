package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.util.Collection;

public interface BookingService {
    BookingItemDto create(long userId, BookingDto bookingDto);

    BookingItemDto approve(long userId, long bookingId, boolean approved);

    BookingItemDto findBookingById(long userId, long bookingId);

    Collection<BookingItemDto> findAllBookingByUser(long userId, String state);

    Collection<BookingItemDto> findAllBookingsByOwner(long ownerId, String state);
}
