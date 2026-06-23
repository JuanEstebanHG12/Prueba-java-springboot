package com.riwi.dbmanager.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApplicationRequest(
        @NotNull(message = "Candidate is required")
        Long candidateId,

        @NotNull(message = "Vacancy is required")
        Long vacancyId,

        @Size(max = 500, message = "Observations must not exceed 500 characters")
        String observations
) {
}
