package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {
    private long id;
    @NonNull
    private String name;
    @NonNull
    @NotBlank(message = "email не может быть пустым.")
    @Email(message = "Некорректный email.")
    private String email;

    public UserDto() {
        super();
    }
}
