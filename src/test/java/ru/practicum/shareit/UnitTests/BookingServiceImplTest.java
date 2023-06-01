package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoValidator;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private BookingDtoValidator validator = new BookingDtoValidator();
    @InjectMocks
    private BookingServiceImpl bookingService;
    private final User user = new User(1L, "user", "user@mail");
    private final User user2 = new User(2L, "user", "user2@mail");
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("testItem")
            .owner(user)
            .available(true).build();
    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(item.getId())
            .start(LocalDateTime.now().plusHours(1L))
            .end(LocalDateTime.now().plusHours(2L)).build();
    private final Booking booking = BookingMapper.toBooking(bookingDto, item, user2, Status.WAITING);
    private static final LocalDateTime DATE_TIME = LocalDateTime.now();

    @Test
    void create_whenUserNotFound_thenInvalidParameterException() {
        when(userService.findUser(user.getId())).thenThrow(InvalidParameterException.class);

        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> bookingService.create(user.getId(), bookingDto));

        assertEquals("Нельзя создать аренду для несуществующего пользователя с id = 1",
                exception.getMessage());
        verify(userService).findUser(user.getId());
    }

    @Test
    void create_whenItemNotFound_thenInvalidParameterException() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(itemService.findItem(bookingDto.getItemId())).thenThrow(InvalidParameterException.class);

        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> bookingService.create(user.getId(), bookingDto));

        assertEquals("Нельзя создать аренду для несуществующего вещи с id = 1",
                exception.getMessage());
        verify(itemService).findItem(bookingDto.getItemId());
    }

    @Test
    void create_whenItemIsNotAvailable_thenValidationException() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        item.setAvailable(false);
        when(itemService.findItem(bookingDto.getItemId())).thenReturn(item);


        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(user.getId(), bookingDto));

        assertEquals("Вещь недоступна для аренды",
                exception.getMessage());
    }

    @Test
    void create_whenUserIdEqualsItemOwnerId_thenInvalidParameterException() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(itemService.findItem(bookingDto.getItemId())).thenReturn(item);

        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> bookingService.create(user.getId(), bookingDto));

        assertEquals("Пользователь не может аредновать свою вещь!",
                exception.getMessage());
    }

    @Test
    void create_whenBookingDtoIsValid_thenBookingItemDtoReturned() {
        when(userService.findUser(user2.getId())).thenReturn(UserMapper.toUserDto(user2));
        when(itemService.findItem(bookingDto.getItemId())).thenReturn(item);
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingItemDto actual = bookingService.create(user2.getId(), bookingDto);

        assertEquals(BookingMapper.toBookingItemDto(booking), actual);
        verify(bookingRepository).save(booking);
    }

    @Test
    void approve_whenUserNotOwnerOfBooking_thenInvalidParameterException() {
        booking.setId(1L);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> bookingService.approve(user2.getId(), booking.getId(), true));

        assertEquals("Данный пользователь не может поменять статус вещи!", exception.getMessage());
    }

    @Test
    void approve_whenStatusApproved_thenValidationException() {
        booking.setId(1L);
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.approve(user.getId(), booking.getId(), true));

        assertEquals("У аренды нельзя повторно поменять статус!", exception.getMessage());
    }

    @Test
    void approve_whenApprovedTrue_thenReturnedApprovedBookingItemDto() {
        booking.setId(1L);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingItemDto expected = BookingMapper.toBookingItemDto(booking);
        expected.setStatus(Status.APPROVED);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingItemDto actual = bookingService.approve(user.getId(), booking.getId(), true);

        assertEquals(expected, actual);
        verify(bookingRepository).save(booking);
    }

    @Test
    void approve_whenApprovedFalse_thenReturnedRejectedBookingItemDto() {
        booking.setId(1L);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingItemDto expected = BookingMapper.toBookingItemDto(booking);
        expected.setStatus(Status.REJECTED);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingItemDto actual = bookingService.approve(user.getId(), booking.getId(), false);

        assertEquals(expected, actual);
        verify(bookingRepository).save(booking);
    }

    @Test
    void findBookingById_whenBookingNotFound_thenInvalidParameterException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> bookingService.findBookingById(user.getId(), 1L));

        assertEquals("Аренда не найдена", exception.getMessage());
        verify(bookingRepository).findById(1L);
    }

    @Test
    void findBookingById_whenBookingNotAvailableForUser_thenInvalidParameterException() {
        booking.setId(1L);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        item.setOwner(new User());

        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> bookingService.findBookingById(user.getId(), 1L));

        assertEquals("Искомая аренад недоступна для данного юзера", exception.getMessage());
        verify(bookingRepository).findById(1L);
    }

    @Test
    void findBookingById_whenBookingAvailableForUser_thenBookingItemDtoReturned() {
        booking.setId(1L);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingItemDto actual = bookingService.findBookingById(user.getId(), 1L);

        assertEquals(BookingMapper.toBookingItemDto(booking), actual);
        verify(bookingRepository).findById(1L);
    }

    @Test
    void findAllBookingByUser_whenUnknownState_thenDataBaseException() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));

        DataBaseException exception = assertThrows(DataBaseException.class,
                () -> bookingService.findAllBookingByUser(user.getId(), "Unknown", 1, 1, DATE_TIME));

        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void findAllBookingByUser_whenStateEmpty_thenBookingItemDtoReturned() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId(), PageRequest.of(1, 1)))
                .thenReturn(List.of(booking));

        Collection<BookingItemDto> actual = bookingService.findAllBookingByUser(
                user.getId(), "ALL", 1, 1, DATE_TIME);

        assertArrayEquals(List.of(BookingMapper.toBookingItemDto(booking)).toArray(), actual.toArray());

    }

    @Test
    void findAllBookingByUser_whenStateCurrent_thenBookingItemDtoReturned() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(user.getId(),
                DATE_TIME, DATE_TIME,
                PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "end"))))
                .thenReturn(List.of(booking));

        Collection<BookingItemDto> actual = bookingService.findAllBookingByUser(
                user.getId(), "CURRENT", 1, 1, DATE_TIME);

        assertArrayEquals(List.of(BookingMapper.toBookingItemDto(booking)).toArray(), actual.toArray());
    }

    @Test
    void findAllBookingByUser_whenStatePast_thenBookingItemDtoReturned() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByBookerIdAndEndIsBefore(user.getId(), DATE_TIME,
                PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "end"))))
                .thenReturn(List.of(booking));

        Collection<BookingItemDto> actual = bookingService.findAllBookingByUser(
                user.getId(), "PAST", 1, 1, DATE_TIME);

        assertArrayEquals(List.of(BookingMapper.toBookingItemDto(booking)).toArray(), actual.toArray());
    }

    @Test
    void findAllBookingByUser_whenStateFUTURE_thenBookingItemDtoReturned() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByBookerIdAndStartIsAfter(user.getId(), DATE_TIME,
                PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "end"))))
                .thenReturn(List.of(booking));

        Collection<BookingItemDto> actual = bookingService.findAllBookingByUser(
                user.getId(), "FUTURE", 1, 1, DATE_TIME);

        assertArrayEquals(List.of(BookingMapper.toBookingItemDto(booking)).toArray(), actual.toArray());
    }

    @Test
    void findAllBookingByUser_whenStateWAITING_thenBookingItemDtoReturned() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByBookerIdAndStatus(user.getId(), Status.WAITING,
                PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "end"))))
                .thenReturn(List.of(booking));

        Collection<BookingItemDto> actual = bookingService.findAllBookingByUser(
                user.getId(), "WAITING", 1, 1, DATE_TIME);

        assertArrayEquals(List.of(BookingMapper.toBookingItemDto(booking)).toArray(), actual.toArray());
    }

    @Test
    void findAllBookingByUser_whenStateREJECTED_thenBookingItemDtoReturned() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByBookerIdAndStatus(user.getId(), Status.REJECTED,
                PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "end"))))
                .thenReturn(List.of(booking));

        Collection<BookingItemDto> actual = bookingService.findAllBookingByUser(
                user.getId(), "REJECTED", 1, 1, DATE_TIME);

        assertArrayEquals(List.of(BookingMapper.toBookingItemDto(booking)).toArray(), actual.toArray());
    }

    @Test
    void findAllBookingByOwner_whenStateEmpty_thenBookingItemDtoReturned() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByOwner(user.getId(), PageRequest.of(1, 1)))
                .thenReturn(List.of(booking));

        Collection<BookingItemDto> actual = bookingService.findAllBookingsByOwner(
                user.getId(), "ALL", 1, 1);

        assertArrayEquals(List.of(BookingMapper.toBookingItemDto(booking)).toArray(), actual.toArray());

    }

    @Test
    void findAllBookingByOwner_whenStateCurrent_thenBookingItemDtoReturned() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByOwnerAndCurrentState(user.getId(),
                PageRequest.of(1, 1)))
                .thenReturn(List.of(booking));

        Collection<BookingItemDto> actual = bookingService.findAllBookingsByOwner(
                user.getId(), "CURRENT", 1, 1);

        assertArrayEquals(List.of(BookingMapper.toBookingItemDto(booking)).toArray(), actual.toArray());
    }

    @Test
    void findAllBookingByOwner_whenStatePast_thenBookingItemDtoReturned() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByOwnerAndPastState(user.getId(),
                PageRequest.of(1, 1)))
                .thenReturn(List.of(booking));

        Collection<BookingItemDto> actual = bookingService.findAllBookingsByOwner(
                user.getId(), "PAST", 1, 1);

        assertArrayEquals(List.of(BookingMapper.toBookingItemDto(booking)).toArray(), actual.toArray());
    }

    @Test
    void findAllBookingByOwner_whenStateFUTURE_thenBookingItemDtoReturned() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByOwnerAndFutureState(user.getId(),
                PageRequest.of(1, 1)))
                .thenReturn(List.of(booking));

        Collection<BookingItemDto> actual = bookingService.findAllBookingsByOwner(
                user.getId(), "FUTURE", 1, 1);

        assertArrayEquals(List.of(BookingMapper.toBookingItemDto(booking)).toArray(), actual.toArray());
    }

    @Test
    void findAllBookingByOwner_whenStateWAITING_thenBookingItemDtoReturned() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByOwnerAndState(user.getId(), Status.WAITING,
                PageRequest.of(1, 1)))
                .thenReturn(List.of(booking));

        Collection<BookingItemDto> actual = bookingService.findAllBookingsByOwner(
                user.getId(), "WAITING", 1, 1);

        assertArrayEquals(List.of(BookingMapper.toBookingItemDto(booking)).toArray(), actual.toArray());
    }

    @Test
    void findAllBookingByOwner_whenStateREJECTED_thenBookingItemDtoReturned() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByOwnerAndState(user.getId(), Status.REJECTED,
                PageRequest.of(1, 1)))
                .thenReturn(List.of(booking));

        Collection<BookingItemDto> actual = bookingService.findAllBookingsByOwner(
                user.getId(), "REJECTED", 1, 1);

        assertArrayEquals(List.of(BookingMapper.toBookingItemDto(booking)).toArray(), actual.toArray());
    }
}