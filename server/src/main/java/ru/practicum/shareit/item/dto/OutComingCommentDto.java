package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OutComingCommentDto {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}