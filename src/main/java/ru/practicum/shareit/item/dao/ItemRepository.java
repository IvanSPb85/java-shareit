package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwnerId(Long ownerId);

    @Query("select i from Item i where upper(i.name) like upper(concat('%', ?1, '%'))" +
            " or upper(i.description) like upper(concat('%', ?1, '%')) and available = true")
    Collection<Item> search(String text);
}
