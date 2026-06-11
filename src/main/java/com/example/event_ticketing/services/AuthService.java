package com.example.event_ticketing.services;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.event_ticketing.dto.request.LoginRequest;
import com.example.event_ticketing.dto.request.RegisterRequest;
import com.example.event_ticketing.dto.response.AuthResponse;
import com.example.event_ticketing.models.User;
import com.example.event_ticketing.models.enums.Role;
import com.example.event_ticketing.repositories.UserRepository;
import com.example.event_ticketing.security.JwtService;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        Optional<User> emailRegistered = userRepository.findByEmail(request.getEmail());

        if(emailRegistered.isPresent()) {
            throw new RuntimeException("Email sudah terdaftar");
        }

        User user = User.builder()
                .nama(request.getNama())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}
