package com.riwi.dbmanager.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(

        @NotNull(message = "Email is required")
        String email,

        @NotNull(message = "Password is required")
        String password
) {
}
