package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
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
    @Transactional
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
        // находим в базе Список Отзывов по вещи
        Collection<Comment> allComments = commentRepository.findAllByItemId(itemId);
        // если пользователь является владельцем вещи,
        if (item.getOwner().getId() == userId) {
            // находим в базе отсортированный по времени Список одобренных Бронирований по вещи
            Collection<Booking> bookings = bookingRepository
                    .findAllByItemIdAndStatusOrderByStartAsc(itemId, Status.APPROVED);
            // то добавляем к вещи отзывы и бронирования
            return addLastAndNextBookingToItem(allComments, bookings, item);
        }
        // иначе добавляем к вещи только отзывы
        return ItemMapper.toItemBookingsDto(item, null, null,
                allComments.stream().map(CommentMapper::toOutComingCommentDto).collect(Collectors.toList()));
    }

    @Override
    public Collection<ItemBookingsDto> findItemsByOwner(long userId, Integer from, Integer size) {
        if (from > 0 && size > 0) from = from / size;
        // находим в базе Список Вещей пользователя
        Collection<Item> items = itemRepository.findAllByOwnerId(userId, PageRequest.of(from, size));
        log.info("У пользователя с id = {} найдено {} вещей.", userId, items.size());
        // добавляем к вещам Отзывы и бронирования
        return addDataToItems(items);
    }

    @Override
    public Collection<ItemDto> findItemForRent(long userId, String itemName, Integer from, Integer size) {
        if (itemName.isBlank()) {
            log.info("При поиске вещи по названию получена пустая строка в запросе. Возвращен пустой список.");
            return Collections.emptyList();
        }
        if (from > 0 && size > 0) from = from / size;
        Collection<Item> items = itemRepository.search(itemName, PageRequest.of(from, size));
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
    @Transactional
    public OutComingCommentDto createComment(long userId, long itemId, InComingCommentDto inComingCommentDto) {
        UserDto authorDto = userService.findUser(userId);
        Item item = findItem(itemId);
        if (bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndIsBefore(
                userId, itemId, Status.APPROVED, inComingCommentDto.getCreated())) {
            Comment savedComment = commentRepository.save(
                    CommentMapper.toComment(inComingCommentDto, UserMapper.toUser(authorDto), item));
            return CommentMapper.toOutComingCommentDto(savedComment);
        }
        throw new ValidationException("У вещи нет подтверженного бронирования");
    }

    private Collection<ItemBookingsDto> addDataToItems(Collection<Item> items) {
        // формируем Список Идентификаторов вещей
        List<Long> itemIdList = new ArrayList<>();
        items.forEach(item -> itemIdList.add(item.getId()));
        // получаем из базы данных Список всех Отзывов согласно списку идентификаторов
        // и группируем их в словаре по идентификаторам вещей
        Map<Long, List<Comment>> allComments = commentRepository.findAllByItemIdIn(itemIdList)
                .stream().collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        // получаем из базы данных отсортированный по времени
        // Список всех одобренных Бронирований согласно списку идентификаторов
        // и группируем их в словаре по идентификаторам вещей
        Map<Long, List<Booking>> allBookings = bookingRepository
                .findAllByItemIdInAndStatusOrderByStartAsc(itemIdList, Status.APPROVED)
                .stream().collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        // создаем Новый пустой Список Вещей с отзывами и бронированиями
        Collection<ItemBookingsDto> itemBookingsDtos = new ArrayList<>();
        // для каждой вещи:
        items.forEach(item -> {
            // добавляем Отзывы и Бронирования
            itemBookingsDtos.add(addLastAndNextBookingToItem(allComments.get(item.getId()),
                    allBookings.get(item.getId()), item));
        });
        return itemBookingsDtos;
    }

    private ItemBookingsDto addLastAndNextBookingToItem(Collection<Comment> comments,
                                                        Collection<Booking> bookings, Item item) {
        // создаем новый пустой список отзывов для вещи
        Collection<OutComingCommentDto> outComingCommentDtoList = Collections.emptyList();
        // если отзывы для вещи существуют мапим в потоке Список отзывов для этой вещи
        if (comments != null) outComingCommentDtoList = comments.stream()
                .map(CommentMapper::toOutComingCommentDto).collect(Collectors.toList());

        ShortBookingItemDto lastBooking = null;
        ShortBookingItemDto nextBooking = null;

        // если бронирования для вещи существуют
        if (bookings != null) {
            // создаем обратный список бронирований вещи
            List<Booking> reversedBookings = new ArrayList<>(bookings);
            Collections.reverse(reversedBookings);

            // находим в потоке следующее бронирование
            Optional<Booking> nextBookingOptional = bookings.stream()
                    .dropWhile(booking -> booking.getStart().isBefore(LocalDateTime.now())).findFirst();
            // находим в потоке предыдущее бронирование
            Optional<Booking> lastBookingOptional = reversedBookings.stream()
                    .dropWhile(booking -> booking.getStart().isAfter(LocalDateTime.now())).findFirst();

            if (lastBookingOptional.isPresent())
                // мапим бронирование
                lastBooking = BookingMapper.shortBookingItemDto(lastBookingOptional.get());
            if (nextBookingOptional.isPresent())
                // мапим бронирование
                nextBooking = BookingMapper.shortBookingItemDto(nextBookingOptional.get());
        }
        // мапим вещи с отзывами и бронированиями и возвращаем
        return ItemMapper.toItemBookingsDto(item, lastBooking, nextBooking, outComingCommentDtoList);
    }
}
