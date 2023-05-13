package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService{
    @Override
    public BookingDto create(long userId, BookingDto bookingDto) {
        return null;
    }

    @Override
    public BookingDto approve(long userId, long bookingId, boolean approved) {
        return null;
    }

    @Override
    public BookingDto findBookingById(long userId, long bookingId) {
        return null;
    }

    @Override
    public Collection<BookingDto> findAllBookingByUser(long userId, String state) {
        return null;
    }

    @Override
    public Collection<BookingDto> findAllBookingsByOwner(long ownerId, String state) {
        return null;
    }
}
