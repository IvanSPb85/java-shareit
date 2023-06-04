package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class ItemDto {
    private long id;
    @Length(max = 255)
    @NotBlank
    private String name;
    @Length(max = 2000)
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}
