package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.InComingCommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OutComingCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("itemDto")
            .description("test")
            .available(true).build();
    private final User user = new User(1L, "user", "user@mail");
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("testItem")
            .owner(user)
            .available(true).build();
    InComingCommentDto inComingCommentDto = new InComingCommentDto();


    @Test
    void create_whenUserNotFound_thenInvalidParameterExceptionThrown() {
        when(userService.findUser(1L)).thenThrow(InvalidParameterException.class);

        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> itemService.create(1L, itemDto));

        assertEquals("Нельзя сохранить вещь для несуществующего пользователя с id = 1",
                exception.getMessage());
    }

    @Test
    void create_whenUserFound_thenReturnedItemDto() {
        when(userService.findUser(1L)).thenReturn(UserMapper.toUserDto(user));
        Item expectedItem = ItemMapper.toItem(itemDto, user);
        when(itemRepository.save(expectedItem)).thenReturn(expectedItem);

        ItemDto actualItemDto = itemService.create(user.getId(), itemDto);

        assertEquals(ItemMapper.toItemDto(expectedItem), actualItemDto);
        verify(itemRepository).save(expectedItem);
    }

    @Test
    void update_whenUserNotOwnerOfItem_thenInvalidParameterExceptionThrown() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> itemService.update(2L, item.getId(), itemDto));

        assertEquals("Пользователь с id = 2 не имеет доступа к вещи с id = 1",
                exception.getMessage());
    }

    @Test
    void update_whenAllFieldsOfItemDtoNotNull_thenReturnedUpdateItem() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto updateItem = itemService.update(1L, 1L, itemDto);

        assertEquals(itemDto, updateItem);
    }

    @Test
    void findItemById_whenItemFound_thenReturnedItemBookingsDto() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemIdIn(List.of(item.getId()))).thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemIdInAndStatus(List.of(item.getId()), Status.APPROVED))
                .thenReturn(Collections.emptyList());

        ItemBookingsDto actual = itemService.findItemById(user.getId(), itemDto.getId());

        assertEquals(ItemMapper.toItemBookingsDto(
                item, null, null, Collections.emptyList()), actual);
    }

    @Test
    void findItemsByOwner_whenIfPositiveAndSizePositive_thenReturnedCollectionOfItem() {
        when(itemRepository.findAllByOwnerId(user.getId(), PageRequest.of(1, 1))).thenReturn(List.of(item));

        Collection<ItemBookingsDto> actual = itemService.findItemsByOwner(user.getId(), 1, 1);

        assertEquals(List.of(ItemMapper.toItemBookingsDto(
                item, null, null, Collections.emptyList())), actual);
    }

    @Test
    void findItemForRent_whenNameIsBlank_thenReturnedEmptyList() {
        Collection<ItemDto> actual = itemService.findItemForRent(user.getId(), "", 1, 1);

        assertTrue(actual.isEmpty());
    }

    @Test
    void findItemForRent_whenNameIsNotBlank_thenReturnedCollectionOfItemDto() {
        when(itemRepository.search("item", PageRequest.of(1, 1)))
                .thenReturn(List.of(item));

        Collection<ItemDto> actual = itemService.findItemForRent(user.getId(), "item", 1, 1);

        assertEquals(List.of(ItemMapper.toItemDto(item)), actual);
        verify(itemRepository).search("item", PageRequest.of(1, 1));
    }

    @Test
    void findItem_whenItemNotFound_thenInvalidParameterExceptionThrown() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> itemService.findItem(item.getId()));

        assertEquals("Вещь с id = 1 не найдена в базе.", exception.getMessage());
        verify(itemRepository).findById(item.getId());
    }

    @Test
    void createComment_whenNotExistBooking_thenValidationExceptionThrown() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndIsBefore(
                user.getId(), item.getId(), Status.APPROVED, inComingCommentDto.getCreated())).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.createComment(user.getId(), item.getId(), inComingCommentDto));

        assertEquals("У вещи нет подтверженного бронирования", exception.getMessage());
        verify(bookingRepository).existsByBookerIdAndItemIdAndStatusAndEndIsBefore(
                user.getId(), item.getId(), Status.APPROVED, inComingCommentDto.getCreated());
    }

    @Test
    void createComment_whenExistBooking_thenOutComingCommentDtoReturned() {
        when(userService.findUser(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndIsBefore(
                user.getId(), item.getId(), Status.APPROVED, inComingCommentDto.getCreated())).thenReturn(true);
        Comment comment = CommentMapper.toComment(inComingCommentDto, user, item);
        when(commentRepository.save(comment)).thenReturn(comment);

        OutComingCommentDto actual = itemService.createComment(user.getId(), item.getId(), inComingCommentDto);

        assertEquals(CommentMapper.toOutComingCommentDto(comment), actual);
        verify(commentRepository).save(comment);
    }
}