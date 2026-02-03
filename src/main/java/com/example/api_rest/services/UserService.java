package com.example.api_rest.services;

import java.util.Date;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.api_rest.models.UserModel;
import com.example.api_rest.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
    }

    public UserModel registerUser(UserModel user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("USER"));
        user.setEnabled(true);
        user.setFechaCreacion(new Date());
        user.setFechaUltimoAcceso(new Date());

        return userRepository.save(user);
    }

    public void updateLastAccess(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setFechaUltimoAcceso(new Date());
            userRepository.save(user);
        });
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
