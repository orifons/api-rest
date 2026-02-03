package com.example.api_rest.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.api_rest.dtos.request.AuthRequest;
import com.example.api_rest.dtos.request.RegisterRequest;
import com.example.api_rest.dtos.response.AuthResponse;
import com.example.api_rest.models.UserModel;
import com.example.api_rest.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    public final UserService userService;

    public AuthResponse login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(),
                authRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserModel user = (UserModel) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        userService.updateLastAccess(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .roles(user.getRoles())
                .build();
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        UserModel user = new UserModel();
        user.setNombre(registerRequest.getNombre());
        user.setApellido(registerRequest.getApellido());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());

        UserModel savedUser = userService.registerUser(user);

        String token = jwtUtil.generateToken(savedUser);
        String refreshToken = jwtUtil.generateRefreshToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .email(savedUser.getEmail())
                .nombre(savedUser.getNombre())
                .apellido(savedUser.getApellido())
                .roles(savedUser.getRoles())
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtUtil.extractUsername(refreshToken);
        UserModel user = (UserModel) userService.loadUserByUsername(email);

        if (!jwtUtil.validateToken(refreshToken, user)) {
            throw new RuntimeException("Refresh token inválido");
        }

        String newToken = jwtUtil.generateToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .email(user.getEmail())
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .roles(user.getRoles())
                .build();
    }
}
