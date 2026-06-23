package com.riwi.dbmanager.dto.request;

import com.riwi.dbmanager.model.enums.JobStatus;
import jakarta.validation.constraints.NotNull;

public record VacancyStatusRequest(
        @NotNull(message = "Status is required")
        JobStatus status
) {}
