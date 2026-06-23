package com.riwi.dbmanager.dto.response;

public record TokenResponse(
        String token,
        String refreshToken,
        String role
) {
}
