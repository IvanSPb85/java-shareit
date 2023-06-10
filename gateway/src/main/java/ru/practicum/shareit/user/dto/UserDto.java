package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;

    @NonNull
    @Length(max = 255)
    private String name;

    @NonNull
    @NotBlank(message = "email не может быть пустым.")
    @Email(message = "Некорректный email.")
    @Length(max = 512)
    private String email;
}

