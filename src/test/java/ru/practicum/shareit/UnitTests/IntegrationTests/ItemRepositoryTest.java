package ru.practicum.shareit.UnitTests.IntegrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private final User user = new User(1L, "user", "user@mail");
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("item for rent")
            .available(true)
            .owner(user)
            .build();
    private final Item item2 = Item.builder()
            .id(2L)
            .name("name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    @Test
    void findAllByOwnerId() {
    }

    @Test
    void search_whenItemAvailable_thenReturnedItem() {
        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(item2);
        List<Item> result = itemRepository.search("iTeM", PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertEquals(List.of(item), result);
    }

    @Test
    void findAllByRequestIdIn() {
    }
}