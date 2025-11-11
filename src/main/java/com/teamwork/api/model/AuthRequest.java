package com.teamwork.api.model;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
