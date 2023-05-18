package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(InComingCommentDto inComingCommentDto, User author, Item item) {
        return Comment.builder()
                .text(inComingCommentDto.getText())
                .author(author)
                .item(item)
                .created(LocalDateTime.now()).build();

    }

    public static OutComingCommentDto toOutComingCommentDto(Comment comment) {
        return OutComingCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated()).build();
    }
}
