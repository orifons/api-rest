package com.example.api_rest.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private final String type = "Bearer";
    private String email;
    private String nombre;
    private String apellido;
    private java.util.List<String> roles;
}