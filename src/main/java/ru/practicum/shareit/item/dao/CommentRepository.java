package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Collection<Comment> findAllByItemId(long itemId);

    Collection<Comment> findAllByItemIdIn(List<Long> itemIdList);
}
