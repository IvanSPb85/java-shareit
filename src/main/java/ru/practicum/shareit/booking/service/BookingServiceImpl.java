package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoValidator;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingDtoValidator validator;

    @Override
    public BookingItemDto create(long userId, BookingDto bookingDto) {
        User user;
        Item item;
        try {
            user = UserMapper.toUser(userService.findUser(userId));
        } catch (InvalidParameterException e) {
            log.warn("Нельзя создать аренду для несуществующего пользователя с id = {}", userId);
            throw new InvalidParameterException(
                    String.format("Нельзя создать аренду для несуществующего пользователя с id = %d", userId));
        }
        try {
            item = ItemMapper.toItem(itemService.findItemById(userId, bookingDto.getItemId()), user);
        } catch (InvalidParameterException e) {
            log.warn("Нельзя создать аренду для несуществующего вещи с id = {}", userId);
            throw new InvalidParameterException(
                    String.format("Нельзя создать аренду для несуществующего вещи с id = %d", userId));
        }
        if (!item.isAvailable()) throw new ValidationException("Вещь недоступна для аренды");
        validator.validateBookingTime(bookingDto);
        Booking saveBooking = bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user, Status.WAITING));
        return BookingMapper.toBookingItemDto(saveBooking);

    }

    @Override
    public BookingItemDto approve(long userId, long bookingId, boolean approved) {

        return null;
    }

    @Override
    public BookingItemDto findBookingById(long userId, long bookingId) throws InvalidParameterException {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            throw new InvalidParameterException("Аренда не найдена");
        }
        Booking booking = bookingOptional.get();
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new InvalidParameterException("Искомая аренад недоступна для данного юзера");
        }
        return BookingMapper.toBookingItemDto(booking);
    }

    @Override
    public Collection<BookingItemDto> findAllBookingByUser(long userId, String state) {
        return null;
    }

    @Override
    public Collection<BookingItemDto> findAllBookingsByOwner(long ownerId, String state) {
        return null;
    }
}
