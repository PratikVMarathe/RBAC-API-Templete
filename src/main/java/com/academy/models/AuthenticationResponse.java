package com.academy.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String username;
    private List<Role> roles;
    private String message;
}


