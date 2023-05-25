package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Builder
@Data
public class ItemRequestDto {
    private long id;
    @Length(max = 2000)
    @NotBlank
    private String description;
    private User requestor;
    private LocalDateTime created;
}
