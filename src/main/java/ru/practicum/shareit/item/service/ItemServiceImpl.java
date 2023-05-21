package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ShortBookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        User user;
        try {
            user = UserMapper.toUser(userService.findUser(userId));
        } catch (InvalidParameterException e) {
            log.warn("Нельзя сохранить вещь для несуществующего пользователя с id = {}", userId);
            throw new InvalidParameterException(
                    String.format("Нельзя сохранить вещь для несуществующего пользователя с id = %d", userId));
        }
        Item item = ItemMapper.toItem(itemDto, user);
        Item saveItem = itemRepository.save(item);
        log.info("\"{}\" с id = {} успешно сохранена в базе.",
                saveItem.getName(), saveItem.getId());
        return ItemMapper.toItemDto(saveItem);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item foundItem = findItem(itemId);
        if (foundItem.getOwner().getId() != userId) {
            throw new InvalidParameterException(
                    String.format("Пользователь с id = %d не имеет доступа к вещи с id = %d", userId, itemId));
        }
        if (itemDto.getName() != null) {
            foundItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            foundItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            foundItem.setAvailable(itemDto.getAvailable());
        }
        Item updateItem = itemRepository.save(foundItem);
        log.info("\"{}\" c id = {} успешно обновлена", updateItem.getName(), updateItem.getId());
        return ItemMapper.toItemDto(updateItem);
    }

    @Override
    public ItemBookingsDto findItemById(long userId, long itemId) {
        Item item = findItem(itemId);
        log.info("По id = {} в базе найден(a) \"{}\".", item.getId(), item.getName());
        return addLastAndNextBookingsToItem(List.of(item), userId).stream().findFirst().get();
    }

    @Override
    public Collection<ItemBookingsDto> findItemsByOwner(long userId) {
        Collection<Item> items = itemRepository.findAllByOwnerId(userId);
        log.info("У пользователя с id = {} найдено {} вещей.", userId, items.size());
        return addLastAndNextBookingsToItem(items, userId);
    }

    @Override
    public Collection<ItemDto> findItemForRent(long userId, String itemName) {
        if (itemName.isBlank()) {
            log.info("При поиске вещи по названию получена пустая строка в запросе. Возвращен пустой список.");
            return Collections.emptyList();
        }
        Collection<Item> items = itemRepository.search(itemName);
        log.info("По запросу пользователя с id = {} найдено {} вещей с названием \"{}\".",
                userId, items.size(), itemName);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public Item findItem(long itemId) {
        Optional<Item> result = itemRepository.findById(itemId);
        if (result.isEmpty()) {
            log.info("Вещь с id = {} не найдена в базе.", itemId);
            throw new InvalidParameterException(String.format("Вещь с id = %d не найдена в базе.", itemId));
        }
        return result.get();
    }

    @Override
    public OutComingCommentDto createComment(long userId, long itemId, InComingCommentDto inComingCommentDto) {
        UserDto authorDto = userService.findUser(userId);
        Item item = findItem(itemId);
        if (bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndIsBefore(
                userId, itemId, Status.APPROVED, LocalDateTime.now())) {
            Comment savedComment = commentRepository.save(
                    CommentMapper.toComment(inComingCommentDto, UserMapper.toUser(authorDto), item));
            return CommentMapper.toOutComingCommentDto(savedComment);
        }

        throw new ValidationException("У вещи нет подтверженного бронирования");
    }

    private Collection<ItemBookingsDto> addLastAndNextBookingsToItem(Collection<Item> items, long userId) {
        List<Long> itemIdList = new ArrayList<>();
        items.forEach(item -> itemIdList.add(item.getId()));
        Collection<Comment> allComments = commentRepository.findAllByItemIdIn(itemIdList);
        Collection<Booking> allBookings = bookingRepository.findAllByItemIdInAndStatus(itemIdList, Status.APPROVED);
        Collection<ItemBookingsDto> itemBookingsDtos = new ArrayList<>();

        items.stream().forEach(item -> {
            Collection<OutComingCommentDto> comments = allComments.stream().
                    filter(comment -> comment.getItem().equals(item)).map(
                            CommentMapper::toOutComingCommentDto).collect(Collectors.toList());

            List<Booking> bookings = allBookings.stream().filter(booking -> booking.getItem().equals(item))
                    .sorted(Comparator.comparing(Booking::getStart)).collect(Collectors.toList());
            List<Booking> reversedBookings = new ArrayList<>(bookings);
            Collections.reverse(reversedBookings);

            ShortBookingItemDto lastBooking = null;
            ShortBookingItemDto nextBooking = null;

            if (item.getOwner().getId() == userId) {
                Optional<Booking> nextBookingOptional = bookings.stream()
                        .dropWhile(booking -> booking.getStart().isBefore(LocalDateTime.now())).findFirst();
                Optional<Booking> lastBookingOptional = reversedBookings.stream()
                        .dropWhile(booking -> booking.getStart().isAfter(LocalDateTime.now())).findFirst();

                if (lastBookingOptional.isPresent())
                    lastBooking = BookingMapper.shortBookingItemDto(lastBookingOptional.get());
                if (nextBookingOptional.isPresent())
                    nextBooking = BookingMapper.shortBookingItemDto(nextBookingOptional.get());
            }
            itemBookingsDtos.add(ItemMapper.toItemBookingsDto(
                    item, lastBooking, nextBooking, comments));

        });
        return itemBookingsDtos;
    }
}
