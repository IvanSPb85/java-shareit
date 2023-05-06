package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataBaseException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        try {
            userService.findUser(userId);
        } catch (InvalidParameterException e) {
            log.warn("Нельзя сохранить вещь для несуществующего пользователя с id = {}", userId);
            throw new InvalidParameterException(
                    String.format("Нельзя сохранить вещь для несуществующего пользователя с id = %d", userId));
        }
        Item item = ItemMapper.toItem(itemDto, userId);
        Optional<Item> result = itemStorage.save(item);
        if (result.isPresent()) {
            log.info("\"{}\" с id = {} успешно сохранена в базе.",
                    result.get().getName(), result.get().getId());
            return ItemMapper.toItemDto(result.get());
        }
        log.warn("Ошибка базы данных при сохранении \"{}\".", itemDto.getName());
        throw new DataBaseException(String.format("Ошибка базы данных при сохранении \"%s\".", itemDto.getName()));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item foundItem = findItem(itemId);
        if (foundItem.getOwner() != userId) {
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
        Optional<Item> result = itemStorage.update(foundItem);
        if (result.isPresent()) {
            log.info("\"{}\" c id = {} успешно обновлена", foundItem.getName(), foundItem.getId());
            return ItemMapper.toItemDto(foundItem);
        }
        log.warn("Ошибка базы данных при обновлении вещи с id = {}.", itemId);
        throw new DataBaseException(String.format("Ошибка базы данных при обновлении вещи с id = %d.", itemId));
    }

    @Override
    public ItemDto findItemById(long userId, long itemId) {
        Item item = findItem(itemId);
        log.info("По id = {} в базе найден(a) \"{}\".", item.getId(), item.getName());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> findItemsByOwner(long userId) {
        Collection<Item> items = itemStorage.getItemByOwner(userId);
        log.info("У пользователя с id = {} найдено {} вещей.", userId, items.size());
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> findItemForRent(long userId, String itemName) {
        if (itemName.isBlank()) {
            log.info("При поиске вещи по названию получена пустая строка в запросе. Возвращен пустой список.");
            return Collections.emptyList();
        }
        Collection<Item> items = itemStorage.getItemForRent(itemName);
        log.info("По запросу пользователя с id = {} найдено {} вещей с названием \"{}\".",
                userId, items.size(), itemName);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private Item findItem(long itemId) {
        Optional<Item> result = itemStorage.getItemById(itemId);
        if (result.isEmpty()) {
            log.info("Вещь с id = {} не найдена в базе.", itemId);
            throw new InvalidParameterException(String.format("Вещь с id = %d не найдена в базе.", itemId));
        }
        return result.get();
    }
}
