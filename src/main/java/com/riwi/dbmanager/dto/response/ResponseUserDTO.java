package com.riwi.dbmanager.dto.response;

import java.time.LocalDate;

//DTO to return user data
public record ResponseUserDTO(
        Long id,
        String name,
        String lastName,
        String email
) {
}
