package ru.practicum.shareit.service;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dao.InMemoryItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dao.InMemoryUserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.security.InvalidParameterException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


public class ItemServiceTests {
    private final UserService userService = new UserServiceImpl(new InMemoryUserStorage());
    private final ItemService itemService = new ItemServiceImpl(new InMemoryItemStorage(), userService);
    private final UserDto user = userService.create(new UserDto("TestUser", "TestUser@email"));

    @Test
    public void createItemTest() {
        ItemDto itemDto = itemService.create(1L,
                ItemDto.builder().name("Name").description("description").available(true).build());
        assertEquals(itemDto.getName(), "Name");
        assertEquals(itemDto.getDescription(), "description");
        assertEquals(itemDto.getId(), 1L);
        assertTrue(itemDto.getAvailable());
    }

    @Test
    public void createItemWithNotFoundUserTest() {
        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> itemService.create(10L,
                        ItemDto.builder().name("Name").description("description").available(true).build()));
        assertEquals(exception.getMessage(),
                String.format("Нельзя сохранить вещь для несуществующего пользователя с id = %d", 10L));
    }

    @Test
    public void updateItemTest() {
        ItemDto itemDto = itemService.create(1L,
                ItemDto.builder().name("Name").description("description").available(true).build());
        ItemDto updatedItem = itemService.update(1L, 1L,
                ItemDto.builder().name("newName").description("newDescription").available(false).build());
        assertEquals(updatedItem.getName(), "newName");
        assertEquals(updatedItem.getDescription(), "newDescription");
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    public void updateItemWithFailIdUserTest() {
        ItemDto itemDto = itemService.create(1L,
                ItemDto.builder().name("Name").description("description").available(true).build());
        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> itemService.update(10L, 1L,
                        ItemDto.builder().name("Name").description("description").available(true).build()));
        assertEquals(exception.getMessage(),
                String.format("Пользователь с id = %d не имеет доступа к вещи с id = %d", 10L, 1L));
    }

    @Test
    public void findItemByIdTest() {
        ItemDto itemDto = itemService.create(1L,
                ItemDto.builder().name("Name").description("description").available(true).build());
        assertEquals(itemService.findItemById(1L, 1L), itemDto);
    }

    @Test
    public void findItemByOwnerTest() {
        userService.create(new UserDto("TestUser", "User@email"));

        ItemDto itemDto = itemService.create(1L,
                ItemDto.builder().name("Name").description("description").available(true).build());
        ItemDto secondItem = itemService.create(1L,
                ItemDto.builder().name("Name").description("description").available(true).build());
        ItemDto thirdItem = itemService.create(2L,
                ItemDto.builder().name("Name").description("description").available(true).build());

        assertThat(itemService.findItemsByOwner(1L))
                .hasSize(2).asList().containsAll(List.of(itemDto, secondItem));
        assertThat(itemService.findItemsByOwner(2L)).hasSize(1)
                .asList().containsAll(List.of(thirdItem));
    }

    @Test
    public void findItemForRentTest() {
        ItemDto item = itemService.create(1L,
                ItemDto.builder().name("Item").description("description").available(true).build());
        ItemDto goodItem = itemService.create(1L,
                ItemDto.builder().name("Second").description("good item").available(true).build());

        assertThat(itemService.findItemForRent(1L, "iTEm")).hasSize(2);
        itemService.update(1L, 2L, ItemDto.builder().available(false).build());
        assertThat(itemService.findItemForRent(1L, "iTEm")).hasSize(1)
                .asList().containsAll(List.of(item));
    }
}
