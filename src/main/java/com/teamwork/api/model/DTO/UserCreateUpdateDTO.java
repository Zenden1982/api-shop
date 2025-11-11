package com.teamwork.api.model.DTO;

import com.teamwork.api.model.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateUpdateDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
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
