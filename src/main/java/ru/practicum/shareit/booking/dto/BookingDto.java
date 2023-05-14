package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Data
public class BookingDto {
    @NotNull
    private long itemId;
    @NotNull
    @Future(message = "Нельзя начать аренду в прошлом!")
    private LocalDateTime start;
    @Future(message = "Нельзя завершить аренду в прошлом!")
    @NotNull
    private LocalDateTime end;
}
