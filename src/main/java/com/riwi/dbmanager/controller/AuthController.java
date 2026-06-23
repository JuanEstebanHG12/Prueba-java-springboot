package com.riwi.dbmanager.controller;

import com.riwi.dbmanager.dto.request.LoginRequest;
import com.riwi.dbmanager.dto.request.RegisterRequest;
import com.riwi.dbmanager.dto.response.TokenResponse;
import com.riwi.dbmanager.mapper.UserMapper;
import com.riwi.dbmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/users")
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    //Controller to register a new user
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> saveUser(@Valid @RequestBody RegisterRequest userRequest) {
        TokenResponse tokenResponse = authService.register(userRequest);
        //Convert User to ResponseUserDTO
        //ResponseUserDTO userDTO = userMapper.toResponseUserDTO(user);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        var token = authService.login(loginRequest);
        return ResponseEntity.ok(token);
    }

//    @PostMapping("/refresh-token")
//    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
//        TokenResponse tokenResponse = authService.refreshToken(refreshTokenRequest);
//        return ResponseEntity.ok(tokenResponse);
//    }
}
