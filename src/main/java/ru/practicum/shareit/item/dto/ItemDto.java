package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class ItemDto {
    private long id;
    @Max(50)
    @NotBlank
    private String name;
    @Max(200)
    private String description;
    @NotNull
    private boolean available;
    private long request;
}
