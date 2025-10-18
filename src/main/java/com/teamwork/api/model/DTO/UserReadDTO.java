package com.teamwork.api.model.DTO;

import java.util.List;

import com.teamwork.api.model.Role;
import com.teamwork.api.model.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserReadDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private List<Role> roles;
    private Boolean active;

    public static UserReadDTO toUserReadDTO(User user) {
        return UserReadDTO.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .roles(user.getRoles())
                .active(user.getActive())
                .build();
    }
}
