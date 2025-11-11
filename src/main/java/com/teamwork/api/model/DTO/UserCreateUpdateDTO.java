package com.teamwork.api.model.DTO;

import com.teamwork.api.model.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateUpdateDTO {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать от 3 до 50 символов")
    private String username;

    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;

    @NotBlank(message = "Фамилия не может быть пустой")
    private String lastName;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    // Регулярное выражение для формата номера телефона (например, +79991234567)
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Некорректный формат номера телефона")
    private String phoneNumber;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    private String password;

    public static User toUser(UserCreateUpdateDTO userDTO) {
        return User.builder()
                .username(userDTO.username)
                .firstName(userDTO.firstName)
                .lastName(userDTO.lastName)
                .email(userDTO.email)
                .phoneNumber(userDTO.phoneNumber)
                .passwordHash(userDTO.password) // Пароль будет хешироваться в сервисе
                .active(true)
                .build();
    }
}
