package com.teamwork.api.model.DTO;

import com.teamwork.api.model.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateUpdateDTO {

    @NotBlank(message = "username is required")
    private String username;

    private String firstName;
    private String lastName;

    @Email(message = "email must be valid")
    private String email;

    private String phoneNumber;

    @Size(min = 6, message = "password must be at least 6 characters")
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
