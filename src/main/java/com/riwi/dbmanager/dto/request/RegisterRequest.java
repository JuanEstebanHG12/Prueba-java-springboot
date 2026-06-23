package com.riwi.dbmanager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


//DTO to register a new user
public record RegisterRequest(
        @NotNull(message = "Name is required")
        String name,

        @NotNull(message = "Last name is required")
        String lastName,

        @NotNull(message = "Email is required")
        @Email(message = "Email is not valid")
        String email,

        @NotNull(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Please confirm your password")
        String confirmPassword

) {
}
