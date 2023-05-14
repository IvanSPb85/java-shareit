package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoValidator;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constant.State;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.exception.DataBaseException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

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
            item = itemService.findItem(bookingDto.getItemId());
        } catch (InvalidParameterException e) {
            log.warn("Нельзя создать аренду для несуществующего вещи с id = {}", userId);
            throw new InvalidParameterException(
                    String.format("Нельзя создать аренду для несуществующего вещи с id = %d", userId));
        }
        if (!item.isAvailable()) throw new ValidationException("Вещь недоступна для аренды");
        validator.validateBookingTime(bookingDto);
        if (item.getOwner().getId() == userId)
            throw new InvalidParameterException("Пользователь не может аредновать свою вещь!");
        Booking saveBooking = bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user, Status.WAITING));
        return BookingMapper.toBookingItemDto(saveBooking);

    }

    @Override
    public BookingItemDto approve(long userId, long bookingId, boolean approved) {
        Booking booking = findBooking(bookingId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new InvalidParameterException("Данный пользователь не может поменять статус вещи!");
        }
        if (booking.getStatus().equals(Status.APPROVED))
            throw new ValidationException("У аренды нельзя повторно поменять статус!");
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else booking.setStatus(Status.REJECTED);
        return BookingMapper.toBookingItemDto(bookingRepository.save(booking));
    }

    @Override
    public BookingItemDto findBookingById(long userId, long bookingId) throws InvalidParameterException {
        Booking booking = findBooking(bookingId);
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new InvalidParameterException("Искомая аренад недоступна для данного юзера");
        }
        return BookingMapper.toBookingItemDto(booking);
    }

    @Override
    public Collection<BookingItemDto> findAllBookingByUser(long userId, String state) {
        userService.findUser(userId);
        Collection<Booking> bookings = new ArrayList<>();
        State currentState = getState(state);
        switch (currentState) {
            case ALL: {
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            }
            case CURRENT: {
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "end"));
                break;
            }
            case PAST: {
                bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "end"));
                break;
            }
            case FUTURE: {

                bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "end"));
                break;
            }
            case WAITING: {
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING,
                        Sort.by(Sort.Direction.DESC, "end"));
                break;
            }
            case REJECTED: {
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED,
                        Sort.by(Sort.Direction.DESC, "end"));
                break;
            }
        }
        return bookings.stream().map(BookingMapper::toBookingItemDto).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingItemDto> findAllBookingsByOwner(long ownerId, String state) {
        userService.findUser(ownerId);
        Collection<Booking> bookings = new ArrayList<>();
        State currentState = getState(state);
        switch (currentState) {
            case ALL: {
                bookings = bookingRepository.findAllByOwner(ownerId);
                break;
            }
            case CURRENT: {
                bookings = bookingRepository.findAllByOwnerAndCurrentState(ownerId);
                break;
            }
            case PAST: {
                bookings = bookingRepository.findAllByOwnerAndPastState(ownerId);
                break;
            }
            case FUTURE: {
                bookings = bookingRepository.findAllByOwnerAndFutureState(ownerId);
                break;
            }
            case WAITING: {
                bookings = bookingRepository.findAllByOwnerAndWaitingState(ownerId, Status.WAITING);
                break;
            }
            case REJECTED: {
                bookings = bookingRepository.findAllByOwnerAndWaitingState(ownerId, Status.REJECTED);
                break;
            }
        }
        return bookings.stream().map(BookingMapper::toBookingItemDto).collect(Collectors.toList());
    }

    private Booking findBooking(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            throw new InvalidParameterException("Аренда не найдена");
        }
        return bookingOptional.get();
    }

    private State getState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new DataBaseException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
