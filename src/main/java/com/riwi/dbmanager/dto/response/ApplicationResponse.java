package com.riwi.dbmanager.dto.response;

import com.riwi.dbmanager.model.enums.ApplicationStatus;

import java.time.LocalDate;

public record ApplicationResponse(
        Long id,
        LocalDate applicationDate,
        ApplicationStatus status,
        String observations,
        ResponseUserDTO candidate,
        VacancyResponse vacancy
) {
}
