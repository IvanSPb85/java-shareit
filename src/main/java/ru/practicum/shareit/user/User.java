package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class User {
    private long id;
    private String name;
    @NotBlank(message = "email не может быть пустым.")
    @Email(message = "Некорректный email.")
    private String email;
}
