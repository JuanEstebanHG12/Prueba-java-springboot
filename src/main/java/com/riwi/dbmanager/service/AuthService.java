package com.riwi.dbmanager.service;

import com.riwi.dbmanager.dto.request.LoginRequest;
import com.riwi.dbmanager.dto.request.RegisterRequest;
import com.riwi.dbmanager.dto.response.TokenResponse;
import com.riwi.dbmanager.exception.BusinessException;
import com.riwi.dbmanager.exception.UserNotFoundException;
import com.riwi.dbmanager.model.User;
import com.riwi.dbmanager.model.enums.Role;
import com.riwi.dbmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //PasswordEncoder is a service that handles password encoding
    private final JwtService jwtService; //JwtService is a service that handles JWT operations
    private final AuthenticationManager authenticationManager; //AuthenticationManager is a service that handles authentication

    public TokenResponse register(RegisterRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.email())) {
            throw new BusinessException("Email already exists");
        }
        //Builder pattern -> ayuda a crear objetos de manera mas eficiente
        User user = User.builder()
                .name(userRequest.name())
                .lastName(userRequest.lastName())
                .email(userRequest.email())
                .password(passwordEncoder.encode(userRequest.password()))
                .role(Role.CANDIDATE)
                .build();

        user = userRepository.save(user);

        var token = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        //TODO: Send email

        return new TokenResponse(token, refreshToken, user.getRole().name());
    }

    public TokenResponse login(LoginRequest loginRequest) {
        //Intenta autenticar un usuario pasando el email y password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(), loginRequest.password()
                )
        );
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return new TokenResponse(jwtToken, refreshToken, user.getRole().name());
    }

}
