package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto create(long userId, BookingDto bookingDto);

    BookingDto approve(long userId, long bookingId, boolean approved);

    BookingDto findBookingById(long userId, long bookingId);

    Collection<BookingDto> findAllBookingByUser(long userId, String state);

    Collection<BookingDto> findAllBookingsByOwner(long ownerId, String state);
}
