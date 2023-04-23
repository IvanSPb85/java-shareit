package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    private String name;
    @NotBlank(message = "email не может быть пустым.")
    @Email(message = "Некорректный email.")
    private String email;
}
