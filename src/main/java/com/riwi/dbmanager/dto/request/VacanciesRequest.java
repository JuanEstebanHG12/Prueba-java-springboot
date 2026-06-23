package com.riwi.dbmanager.dto.request;

import com.riwi.dbmanager.model.enums.JobCategory;
import com.riwi.dbmanager.model.enums.JobStatus;
import com.riwi.dbmanager.model.enums.WorkMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record VacanciesRequest(
        @NotNull(message = "Title is required")
        String title,

        @NotNull(message = "Description is required")
        @NotEmpty(message = "Description cannot be empty")
        @Size(min = 10, max = 100, message = "Description must be at least 10 characters long")
        String description,

        @NotNull(message = "Category is required")
        JobCategory category,

        @NotNull(message = "Mode is required")
        WorkMode mode,

        BigDecimal salary,

        @NotNull(message = "Responsible is required")
        Long responsibleId
) {
}
