package ru.practicum.shareit.UnitTests.ItemTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    private final TestEntityManager em;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User saveUser;
    private Item saveItem;
    private Item saveItem2;
    Pageable pageable = PageRequest.of(0, 10);
    TypedQuery<Item> query;


    @BeforeEach
    public void addData() {
        User user = new User(1L, "user", "user@mail");
        saveUser = userRepository.save(user);

        Item item = Item.builder()
                .name("item")
                .description("item for rent")
                .available(true)
                .owner(saveUser)
                .build();
        saveItem = itemRepository.save(item);

        Item item2 = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(saveUser)
                .build();
        saveItem2 = itemRepository.save(item2);

        query = em.getEntityManager()
                .createQuery("select i from Item i where upper(i.name) like upper(concat('%', ?1, '%'))" +
                        " or upper(i.description) like upper(concat('%', ?1, '%')) and i.available = true", Item.class);
    }

    @AfterEach
    public void clearRepo() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void search_whenItemAvailable_thenReturnedSaveItem() {
        List<Item> result = query.setParameter(1, "iTeM").getResultList();

        assertEquals(1, result.size());
        assertEquals(List.of(saveItem), result);
    }

    @Test
    void search_whenItemAvailable_thenReturnedSaveItem2() {
        List<Item> result = query.setParameter(1, "NamE").getResultList();

        assertEquals(1, result.size());
        assertEquals(List.of(saveItem2), result);
    }

    @Test
    void search_whenNotFoundItem_thenReturnedEmptyList() {
        List<Item> result = query.setParameter(1, "abraKadabra").getResultList();

        assertTrue(result.isEmpty());
    }
}