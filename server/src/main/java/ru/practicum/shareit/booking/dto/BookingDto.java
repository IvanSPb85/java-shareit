package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class BookingDto {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
